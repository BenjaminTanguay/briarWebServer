package com.briar.server.patterns.identitymap;

import com.briar.server.constants.Constants;
import com.briar.server.exception.ObjectDeletedException;
import com.briar.server.model.User;
import com.briar.server.model.UserContacts;
import lombok.NonNull;

import java.util.IdentityHashMap;

import static java.lang.Thread.yield;

public class UserContactsIdentityMap {

    private IdentityHashMap<User, UserContactsWrapper> identityMap;

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
        this.identityMap = new IdentityHashMap<User, UserContactsWrapper>();
    }

    public boolean doesUserContactsExists(@NonNull User user) {
        return this.identityMap.containsKey(user);
    }

    public void addUserContacts(@NonNull User user, @NonNull UserContacts userContacts) {
        UserContactsWrapper wrapper = new UserContactsWrapper(userContacts);
        this.identityMap.put(user, wrapper);
    }

    public synchronized UserContacts getUserContacts(@NonNull User user, @NonNull Constants.Lock lock) throws ObjectDeletedException {
        if (!doesUserContactsExists(user)) {
            throw new ObjectDeletedException();
        }
        UserContactsWrapper wrapper = this.identityMap.get(user);

        if (wrapper.isUserContactsToBeDeleted()) {
            throw new ObjectDeletedException();
        }
        switch(lock) {
            case reading:
                return startReading(wrapper);
            case writing:
                return startWriting(wrapper);
            case deleting:
                return startDeleting(user, wrapper);
            default:
                return null;
        }
    }

    public void stopReading(@NonNull User user) {
        UserContactsWrapper wrapper = this.identityMap.get(user);
        wrapper.stopReading();
    }

    public void stopWriting(@NonNull User user) {
        UserContactsWrapper wrapper = this.identityMap.get(user);
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

    private UserContacts startDeleting(User user, UserContactsWrapper wrapper) {
        wrapper.startWriting();
        wrapper.startUserContactsDeleting();
        UserContacts userContacts = wrapper.getUserContacts();
        this.identityMap.remove(user);
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
