package com.briar.server.patterns.unitofwork;


import com.briar.server.exception.DBException;
import com.briar.server.exception.ObjectDeletedException;
import com.briar.server.exception.UserContactAlreadyExistsException;
import com.briar.server.exception.UserContactDoesntExistsException;

public interface ICommit {

    public void commitDB() throws DBException;
    public void commitIdentityMap() throws ObjectDeletedException, UserContactAlreadyExistsException, UserContactDoesntExistsException;

    public void revertDB() throws DBException;
    public void revertIdentityMap() throws ObjectDeletedException, UserContactAlreadyExistsException, UserContactDoesntExistsException;
}
