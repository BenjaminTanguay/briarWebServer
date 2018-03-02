package com.briar.server.services.tasks;


import com.briar.server.exception.DBException;
import com.briar.server.exception.ObjectAlreadyExistsException;
import com.briar.server.exception.ObjectDeletedException;
import com.briar.server.exception.UserContactDoesntExistsException;

public class ModifyUserContact implements ITask {

    @Override
    public void commitDB() throws DBException {

    }

    @Override
    public void commitIdentityMap() throws ObjectDeletedException, ObjectAlreadyExistsException, UserContactDoesntExistsException {

    }

    @Override
    public void revertDB() throws DBException {

    }

    @Override
    public void revertIdentityMap() throws ObjectDeletedException, ObjectAlreadyExistsException, UserContactDoesntExistsException {

    }
}
