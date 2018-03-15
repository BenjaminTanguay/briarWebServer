package com.briar.server.services;

import com.briar.server.constants.Constants;
import com.briar.server.exception.DataCompromisedException;
import com.briar.server.exception.ObjectDeletedException;
import com.briar.server.handler.UserHandler;
import com.briar.server.mapper.UserMapper;
import com.briar.server.model.domainmodelclasses.User;
import com.briar.server.model.returnedtobriarclasses.BriarUser;
import com.briar.server.patterns.identitymap.UserIdentityMap;
import com.briar.server.patterns.unitofwork.UnitOfWork;
import com.briar.server.services.tasks.DeleteUser;
import com.briar.server.services.tasks.InsertNewUser;
import com.briar.server.services.tasks.ModifyUser;

import javax.inject.Inject;

public class UserService {

    private UserIdentityMap userIdentityMap;
    private UnitOfWork unitOfWork;

    @Inject
    UserMapper mapper;

    public UserService() {
        this(UserIdentityMap.getInstance(), UnitOfWork.getInstance());
    }

    // Parametrised constructor for testing purposes
    public UserService(UserIdentityMap userIdentityMap, UnitOfWork unitOfWork) {
        this.userIdentityMap = userIdentityMap;
        this.unitOfWork = unitOfWork;
    }


    public BriarUser convertUserToBriarUser(User user) {
        return new BriarUser(user.getPhoneGeneratedId(), user.getIp(), user.getPort());
    }

    public boolean validateUserParams(User user) {
        return user.getPhoneGeneratedId() != null &&
                user.getIp() != null &&
                user.getPort() != 0 &&
                user.getPassword() != null;
    }

    public boolean authenticate(User user) throws ObjectDeletedException {
        String userName = user.getPhoneGeneratedId();
        User readUser = readUser(userName);
        boolean isAuthenticated = user.getPassword().equals(readUser.getPassword());
        return isAuthenticated;
    }

    public boolean doesUserExists(String userName) {
        User user = null;
        try {
            user = readUser(userName);
        } catch (ObjectDeletedException e) {
            // We don't need to do anything. The user doesn't exist anymore. Value above is null so the method will
            // return false
        }
        return user != null;
    }

    /**
     * For reading purposes only. If you need to modify the user object, use another method.
     * Returns a clone of the user found in the identity map. Modifying the clone is safe.
     * @param userName
     * @return User
     */
    public User readUser(String userName) throws ObjectDeletedException {
        boolean existsInMap = this.userIdentityMap.doesUserExists(userName);
        User user;
        if (existsInMap) {
            try {
                user = this.userIdentityMap.getUser(userName, Constants.Lock.reading);
            } catch (ObjectDeletedException e) {
                user = this.mapper.findUser(userName);
                if (user == null) {
                    throw e;
                } else {
                    this.userIdentityMap.addUser(userName, user);
                }
            }
        } else {
            user = this.mapper.findUser(userName);
            if (user == null) {
                return null;
            } else {
                this.userIdentityMap.addUser(userName, user);
            }
        }
        if (user != null) {
            user = user.clone();
            this.userIdentityMap.stopReading(userName);
        }
        return user;
    }

    /**
     * To add a user to the DB and the IdentityMap
     * @param user
     * @throws DataCompromisedException
     */
    public void addUser(User user) throws DataCompromisedException {
        UserHandler userHandler = new UserHandler(user);
        InsertNewUser insertUserTask = new InsertNewUser(user, userHandler);
        String transactionId = getTransactionId(user);
        this.unitOfWork.registerCommit(transactionId, insertUserTask);
        this.unitOfWork.pushCommit(transactionId);
    }

    /**
     * To modify a user from the DB and the IdentityMap
     * @param user
     * @throws ObjectDeletedException
     * @throws DataCompromisedException
     */
    public void modifyUser(User user) throws ObjectDeletedException, DataCompromisedException {
        String userName = user.getPhoneGeneratedId();
        User oldUser = readUser(userName);
        UserHandler userHandler = new UserHandler(user);
        UserHandler oldUserHandler = new UserHandler(oldUser);
        ModifyUser modifyUserTask = new ModifyUser(user, oldUser, userHandler, oldUserHandler);
        String transactionId = getTransactionId(user);
        this.unitOfWork.registerCommit(transactionId, modifyUserTask);
        this.unitOfWork.pushCommit(transactionId);
    }

    /**
     * To remove a user from the DB and the IdentityMap
     * NOTE: The code this method relies on isn't implemented at the moment.
     * @param user
     * @throws DataCompromisedException
     */
    public void removeUser(User user) throws DataCompromisedException {
        UserHandler userHandler = new UserHandler(user);
        DeleteUser removeUserTask = new DeleteUser(user, userHandler);
        String transactionId = getTransactionId(user);
        this.unitOfWork.registerCommit(transactionId, removeUserTask);
        this.unitOfWork.pushCommit(transactionId);
    }

    private String getTransactionId(User user) {
        long currentThreadId = Thread.currentThread().getId();
        return user.toString() + " CURRENT THREAD ID: " + currentThreadId;
    }
}
