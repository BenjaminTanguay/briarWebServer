package com.briar.server.patterns.identitymap;

import com.briar.server.constants.Constants;
import com.briar.server.exception.ObjectDeletedException;
import com.briar.server.model.domainmodelclasses.UserContacts;
import lombok.NonNull;

import java.util.IdentityHashMap;

import static java.lang.Thread.yield;

public class UserContactsIdentityMap {

    private IdentityHashMap<String, UserContactsWrapper> identityMap;

    private static volatile UserContactsIdentityMap instance;
    private static Object mutex = new Object();

    public static UserContactsIdentityMap getInstance() {
        UserContactsIdentityMap result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new UserContactsIdentityMap();
            }
        }
        return result;
    }

    private UserContactsIdentityMap() {
        this.identityMap = new IdentityHashMap<String, UserContactsWrapper>();
    }

    public boolean doesUserContactsExists(@NonNull String userName) {
        return this.identityMap.containsKey(userName);
    }

    public void addUserContacts(@NonNull String userName, @NonNull UserContacts userContacts) {
        UserContactsWrapper wrapper = new UserContactsWrapper(userContacts);
        this.identityMap.put(userName, wrapper);
    }

    public synchronized UserContacts getUserContacts(@NonNull String userName, @NonNull Constants.Lock lock) throws ObjectDeletedException {
        if (!doesUserContactsExists(userName)) {
            throw new ObjectDeletedException();
        }
        UserContactsWrapper wrapper = this.identityMap.get(userName);

        if (wrapper.isUserContactsToBeDeleted()) {
            throw new ObjectDeletedException();
        }
        switch(lock) {
            case reading:
                return startReading(wrapper);
            case writing:
                return startWriting(wrapper);
            case deleting:
                return startDeleting(userName, wrapper);
            default:
                return null;
        }
    }

    public void stopReading(@NonNull String userName) {
        UserContactsWrapper wrapper = this.identityMap.get(userName);
        wrapper.stopReading();
    }

    public void stopWriting(@NonNull String userName) {
        UserContactsWrapper wrapper = this.identityMap.get(userName);
        wrapper.stopWriting();
    }

    private UserContacts startReading(UserContactsWrapper wrapper) {
        wrapper.startReading();
        return wrapper.getUserContacts();
    }

    private UserContacts startWriting(UserContactsWrapper wrapper) {
        wrapper.startWriting();
        return wrapper.getUserContacts();
    }

    private UserContacts startDeleting(String userName, UserContactsWrapper wrapper) {
        wrapper.startWriting();
        wrapper.startUserContactsDeleting();
        UserContacts userContacts = wrapper.getUserContacts();
        this.identityMap.remove(userName);
        return userContacts;
    }




    private class UserContactsWrapper {

        private UserContacts userContacts;
        private int nbUserReading;
        private boolean isThreadWriting;
        private boolean isToBeDeleted;

        public UserContactsWrapper(UserContacts userContacts) {
            this.userContacts = userContacts;
            this.nbUserReading = 0;
            this.isThreadWriting = false;
            this.isToBeDeleted = false;
        }

        public UserContacts getUserContacts() {
            return this.userContacts;
        }

        public void setUserContacts(UserContacts userContacts) {
            this.userContacts = userContacts;
        }

        public boolean isUserContactsToBeDeleted() {
            return this.isToBeDeleted;
        }

        public void startUserContactsDeleting() {
            this.isToBeDeleted = true;
        }

        public synchronized void startReading() {
            while(this.isThreadWriting) {
                yield();
            }
            ++this.nbUserReading;
        }

        public synchronized void startWriting() {
            while(this.nbUserReading != 0 || this.isThreadWriting) {
                yield();
            }
            startReading();
            this.isThreadWriting = true;
        }

        public synchronized void stopReading() {
            --this.nbUserReading;
        }

        public synchronized void stopWriting() {
            this.isThreadWriting = false;
        }

    }
}
