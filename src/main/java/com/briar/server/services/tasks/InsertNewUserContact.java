package com.briar.server.services.tasks;

import com.briar.server.exception.*;
import com.briar.server.mapper.UserContactMapper;
import com.briar.server.model.domainmodelclasses.UserContact;
import com.briar.server.patterns.identitymap.UserContactsIdentityMap;

import javax.inject.Inject;

public class InsertNewUserContact implements ITask {

    private UserContact userContactToAdd;
    private UserContactsIdentityMap map;

    @Inject
    private UserContactMapper userContactMapper;

    public InsertNewUserContact(UserContact userContactToAdd) {
        this.userContactToAdd = userContactToAdd;
        this.map = UserContactsIdentityMap.getInstance();
    }

    @Override
    public void commitDB() throws DBException {
        try {
            userContactMapper.addNewUserContact(this.userContactToAdd);
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void commitIdentityMap() throws ObjectDeletedException, ObjectAlreadyExistsException, UserContactDoesntExistsException, IncompleteObjectException {
        String firstUserName = this.userContactToAdd.getFirstUser();
        String secondUserName = this.userContactToAdd.getSecondUser();
        if (firstUserName == null || secondUserName == null) {
            throw new IncompleteObjectException(this.userContactToAdd.toString());
        }
//        if (this.map.doesUserExists(userName)) {
//            throw new ObjectAlreadyExistsException();
//        }
//        this.map.addUser(this.userContactToAdd);
    }

    @Override
    public void revertDB() throws DBException {
        try {
            userContactMapper.removeSpecificUserContact(this.userContactToAdd);
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void revertIdentityMap() throws ObjectDeletedException, ObjectAlreadyExistsException, UserContactDoesntExistsException, IncompleteObjectException {
//        String userName = this.userContactToAdd.getPhoneGeneratedId();
//        if (userName == null) {
//            throw new IncompleteObjectException(this.userContactToAdd.toString());
//        }
//        if (this.map.doesUserExists(userName)) {
//            this.map.getUser(userName, Constants.Lock.deleting);
//            this.map.stopWriting(userName);
//        }
    }
}
