package com.briar.server.services.tasks;

import com.briar.server.exception.*;
import com.briar.server.handler.UserContactsHandler;
import com.briar.server.model.domainmodelclasses.UserContact;

public class InsertNewUserContact extends AbstractUserContactTask {

    public InsertNewUserContact(UserContact userContactToAdd, UserContactsHandler handler) {
        super(userContactToAdd, handler);
    }

    @Override
    public void commitDB() throws DBException {
        try {
            userContactMapper.addNewUserContact(this.userContact);
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
            userContactMapper.removeSpecificUserContact(this.userContact);
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void revertIdentityMap() throws ObjectDeletedException, ObjectAlreadyExistsException, UserContactDoesntExistsException, IncompleteObjectException {
        handler.remove();
    }
}
