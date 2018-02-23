package com.briar.server.patterns.unitofwork;


import com.briar.server.exception.DBException;
import com.briar.server.exception.ObjectDeletedException;
import com.briar.server.exception.UserContactAlreadyExistsException;
import com.briar.server.exception.UserContactDoesntExistsException;

public class ModifyUserContact implements ICommit {

    @Override
    public void commitDB() throws DBException {

    }

    @Override
    public void commitIdentityMap() throws ObjectDeletedException, UserContactAlreadyExistsException, UserContactDoesntExistsException {

    }

    @Override
    public void revertDB() throws DBException {

    }

    @Override
    public void revertIdentityMap() throws ObjectDeletedException, UserContactAlreadyExistsException, UserContactDoesntExistsException {

    }
}
