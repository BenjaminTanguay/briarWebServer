package com.briar.server.patterns.unitofwork;

import com.briar.server.constants.Constants;
import com.briar.server.exception.DBException;
import com.briar.server.exception.DataCompromisedException;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import static java.lang.Thread.yield;

public class UnitOfWork {

    private HashMap<String, ArrayList<ICommit>> toPush;
    private HashMap<String, Stack<ICommit>> toRevert;
    private HashMap<String, CommitLocks> commitLocksCollection;

    private static volatile UnitOfWork instance;
    private static Object mutex = new Object();

    public static UnitOfWork getInstance() {
        UnitOfWork result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new UnitOfWork();
            }
        }
        return result;
    }

    private UnitOfWork() {
        this.toPush = new HashMap<String, ArrayList<ICommit>>();
        this.toRevert = new HashMap<String, Stack<ICommit>>();
        this.commitLocksCollection = new HashMap<String, CommitLocks>();
    }

    public void registerCommit(String transactionId, ICommit toBeCommitted) {
        startReading(transactionId);
        ArrayList<ICommit> commitList = getCommitList(transactionId);
        commitList.add(toBeCommitted);
        stopReading(transactionId);
    }

    public synchronized void pushCommit(String transactionId) throws DataCompromisedException {
        startWriting(transactionId);
        ArrayList<ICommit> commitList = getCommitList(transactionId);
        Stack<ICommit> revertList = getRollbackList(transactionId);

        try {
            for (ICommit commit : commitList) {
                commit.commitDB();
                revertList.push(commit);
                commit.commitIdentityMap();
            }
        } catch (DBException e) {
            revertCommit(transactionId, Constants.LastCommitActionSuccessful.database);
        } catch (Exception e) {
            revertCommit(transactionId, Constants.LastCommitActionSuccessful.identityMap);
        } finally {
            stopWriting(transactionId);
        }
    }

    public synchronized void revertCommit(String transactionId, Constants.LastCommitActionSuccessful commitStatus) throws DataCompromisedException {
        Stack<ICommit> revertList = getRollbackList(transactionId);
        try {

            // Exception occured while trying to push stuff to the database
            if (commitStatus == Constants.LastCommitActionSuccessful.database) {
                ICommit firstCommitToRevert = revertList.pop();
                firstCommitToRevert.revertDB();
            }

            // All other exceptions
            while(!revertList.isEmpty()){
                ICommit commit = revertList.pop();
                commit.revertDB();
                commit.revertIdentityMap();
            }
        } catch(Exception e) {
            throw new DataCompromisedException();
        }
    }

    private ArrayList<ICommit> getCommitList(String transactionId) {
        ArrayList<ICommit> listOfCommits;
        if (this.toPush.containsKey(transactionId)) {
            listOfCommits = this.toPush.get(transactionId);
        } else {
            listOfCommits = new ArrayList<ICommit>();
            this.toPush.put(transactionId, listOfCommits);
        }
        return listOfCommits;
    }

    private Stack<ICommit> getRollbackList(String transactionId) {
        Stack<ICommit> rollbackStack;
        if (this.toRevert.containsKey(transactionId)) {
            rollbackStack = this.toRevert.get(transactionId);
        } else {
            rollbackStack = new Stack<ICommit>();
            this.toRevert.put(transactionId, rollbackStack);
        }
        return rollbackStack;
    }

    private void startReadWriteAction(String transactionId, Constants.Lock lock) {
        boolean isFirstTransaction = this.commitLocksCollection.containsKey(transactionId);
        if (isFirstTransaction) {
            CommitLocks commitLocks = new CommitLocks();
            commitLocks.startReadWriteAction(lock);
            this.commitLocksCollection.put(transactionId, commitLocks);
        } else {
            CommitLocks commitLocks = this.commitLocksCollection.get(transactionId);
            commitLocks.startReadWriteAction(lock);
        }
    }

    private void startReading(String transactionId) {
        startReadWriteAction(transactionId, Constants.Lock.reading);
    }

    private void startWriting(String transactionId) {
        startReadWriteAction(transactionId, Constants.Lock.writing);
    }

    private void stopWriting(String transactionId) {
        CommitLocks commitLocks = this.commitLocksCollection.get(transactionId);
        commitLocks.stopWriting();
    }

    private void stopReading(String transactionId) {
        CommitLocks commitLocks = this.commitLocksCollection.get(transactionId);
        commitLocks.stopReading();
    }

    private class CommitLocks {

        private int nbUserAddingToCommitList;
        private boolean isPushingRightNow;


        public CommitLocks() {
            this.nbUserAddingToCommitList = 0;
            this.isPushingRightNow = false;
        }

        public synchronized void startReadWriteAction(@NonNull Constants.Lock lock) {
            if (lock == Constants.Lock.reading) {
                startReading();
            } else if (lock == Constants.Lock.writing) {
                startWriting();
            }
        }

        private synchronized void startReading() {
            while (this.isPushingRightNow) {
                yield();
            }
            ++this.nbUserAddingToCommitList;
        }

        private synchronized void startWriting() {
            while (this.nbUserAddingToCommitList != 0 || this.isPushingRightNow) {
                yield();
            }
            startReading();
            this.isPushingRightNow = true;
        }

        public synchronized void stopReading() {
            --this.nbUserAddingToCommitList;
        }

        public synchronized void stopWriting() {
            this.isPushingRightNow = false;
        }

    }


}
