package com.briar.server.patterns.identitymap;

import com.briar.server.constants.Constants;
import com.briar.server.exception.ObjectDeletedException;
import com.briar.server.model.User;
import lombok.NonNull;

import java.util.IdentityHashMap;

import static java.lang.Thread.yield;

public class UserIdentityMap {

    private IdentityHashMap<String, UserWrapper> identityMap;

    private static volatile UserIdentityMap instance;
    private static Object mutex = new Object();

    public static UserIdentityMap getInstance() {
        UserIdentityMap result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new UserIdentityMap();
            }
        }
        return result;
    }

    private UserIdentityMap() {
        this.identityMap = new IdentityHashMap<String, UserWrapper>();
    }

    public boolean doesUserExists(@NonNull String userId) {
        return this.identityMap.containsKey(userId);
    }

    public void addUser(@NonNull User user) {
        UserWrapper wrapper = new UserWrapper(user);
        this.identityMap.put(user.getPhoneGeneratedId(), wrapper);
    }

    public synchronized User getUser(@NonNull String userId, @NonNull Constants.Lock lock) throws ObjectDeletedException {
        if (!doesUserExists(userId)) {
            throw new ObjectDeletedException();
        }
        UserWrapper wrapper = this.identityMap.get(userId);

        if (wrapper.isUserToBeDeleted()) {
            throw new ObjectDeletedException();
        }
        switch(lock) {
            case reading:
                return startReading(wrapper);
            case writing:
                return startWriting(wrapper);
            case deleting:
                return startDeleting(wrapper);
            default:
                return null;
        }
    }

    public void stopReading(@NonNull String userId) {
        UserWrapper wrapper = this.identityMap.get(userId);
        wrapper.stopReading();
    }

    public void stopWriting(@NonNull String userId) {
        UserWrapper wrapper = this.identityMap.get(userId);
        wrapper.stopWriting();
    }

    private User startReading(UserWrapper wrapper) {
        wrapper.startReading();
        return wrapper.getUser();
    }

    private User startWriting(UserWrapper wrapper) {
        wrapper.startWriting();
        return wrapper.getUser();
    }

    private User startDeleting(UserWrapper wrapper) {
        wrapper.startWriting();
        wrapper.startUserDeleting();
        User user = wrapper.getUser();
        this.identityMap.remove(user.getPhoneGeneratedId());
        return user;
    }



    private class UserWrapper {

        private User user;
        private int nbUserReading;
        private boolean isThreadWriting;
        private boolean isToBeDeleted;

        public UserWrapper(User user) {
            this.user = user;
            this.nbUserReading = 0;
            this.isThreadWriting = false;
            this.isToBeDeleted = false;
        }

        public User getUser() {
            return this.user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public boolean isUserToBeDeleted() {
            return this.isToBeDeleted;
        }

        public void startUserDeleting() {
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
