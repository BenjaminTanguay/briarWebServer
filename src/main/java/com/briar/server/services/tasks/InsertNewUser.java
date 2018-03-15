package com.briar.server.services.tasks;

import com.briar.server.exception.*;
import com.briar.server.handler.UserHandler;
import com.briar.server.model.domainmodelclasses.User;

public class InsertNewUser extends AbstractUserTask {

    public InsertNewUser(User userToAdd, UserHandler handler) {
        super(userToAdd, handler);
    }

    @Override
    public void commitDB() throws DBException {
        try {
            userMapper.addNewUser(this.user);
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void commitIdentityMap() throws ObjectDeletedException, ObjectAlreadyExistsException, UserContactDoesntExistsException, IncompleteObjectException {
        handler.add();
    }

    @Override
    public void revertDB() throws DBException {
        try {
            userMapper.removeUser(this.user);
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void revertIdentityMap() throws ObjectDeletedException, ObjectAlreadyExistsException, UserContactDoesntExistsException, IncompleteObjectException {
        handler.remove();
    }
}
