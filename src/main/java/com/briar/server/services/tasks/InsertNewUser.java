package com.briar.server.services.tasks;

import com.briar.server.constants.Constants;
import com.briar.server.exception.*;
import com.briar.server.mapper.UserMapper;
import com.briar.server.model.domainmodelclasses.User;
import com.briar.server.patterns.identitymap.UserIdentityMap;

import javax.inject.Inject;

public class InsertNewUser implements ITask {

    private User userToAdd;
    private UserIdentityMap map;

    @Inject
    private UserMapper userMapper;

    public InsertNewUser(User userToAdd) {
        this.userToAdd = userToAdd;
        this.map = UserIdentityMap.getInstance();
    }

    @Override
    public void commitDB() throws DBException {
        try {
            userMapper.addNewUser(this.userToAdd);
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void commitIdentityMap() throws ObjectDeletedException, ObjectAlreadyExistsException, UserContactDoesntExistsException, IncompleteObjectException {
        String userName = this.userToAdd.getPhoneGeneratedId();
        if (userName == null) {
            throw new IncompleteObjectException(this.userToAdd.toString());
        }
        if (this.map.doesUserExists(userName)) {
            throw new ObjectAlreadyExistsException();
        }
        this.map.addUser(this.userToAdd);
    }

    @Override
    public void revertDB() throws DBException {
        try {
            userMapper.removeUser(this.userToAdd);
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void revertIdentityMap() throws ObjectDeletedException, ObjectAlreadyExistsException, UserContactDoesntExistsException, IncompleteObjectException {
        String userName = this.userToAdd.getPhoneGeneratedId();
        if (userName == null) {
            throw new IncompleteObjectException(this.userToAdd.toString());
        }
        if (this.map.doesUserExists(userName)) {
            this.map.getUser(userName, Constants.Lock.deleting);
            this.map.stopWriting(userName);
        }
    }
}
