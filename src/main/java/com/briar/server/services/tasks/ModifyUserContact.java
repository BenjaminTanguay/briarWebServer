package com.briar.server.services.tasks;


import com.briar.server.exception.DBException;
import com.briar.server.exception.ObjectAlreadyExistsException;
import com.briar.server.exception.ObjectDeletedException;
import com.briar.server.exception.UserContactDoesntExistsException;
import com.briar.server.handler.UserContactsHandler;
import com.briar.server.model.domainmodelclasses.UserContact;

public class ModifyUserContact extends AbstractUserContactTask {

    private UserContact oldUserContact;
    private UserContactsHandler oldUserContactHandler;

    public ModifyUserContact(UserContact userContactToModify, UserContact oldUserContact, UserContactsHandler handler, UserContactsHandler oldUserContactHandler) {
        super(userContactToModify, handler);
    }

    @Override
    public void commitDB() throws DBException {
        try {
            userContactMapper.modifyUserContact(this.userContact);
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void commitIdentityMap() throws ObjectDeletedException, ObjectAlreadyExistsException, UserContactDoesntExistsException {
        this.handler.modify();
    }

    @Override
    public void revertDB() throws DBException {
        try {
            userContactMapper.modifyUserContact(this.oldUserContact);
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void revertIdentityMap() throws ObjectDeletedException, ObjectAlreadyExistsException, UserContactDoesntExistsException {
        this.oldUserContactHandler.modify();
    }
}
