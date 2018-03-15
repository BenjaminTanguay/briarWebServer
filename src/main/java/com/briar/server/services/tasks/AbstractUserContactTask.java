package com.briar.server.services.tasks;

import com.briar.server.handler.IHandler;
import com.briar.server.handler.UserContactHandler;
import com.briar.server.mapper.UserContactMapper;
import com.briar.server.model.domainmodelclasses.UserContact;

import javax.inject.Inject;

public abstract class AbstractUserContactTask implements ITask {

    protected UserContact userContact;
    protected IHandler handler;

    @Inject
    protected UserContactMapper userContactMapper;

    public AbstractUserContactTask(UserContact userContact, UserContactHandler handler) {
        this.userContact = userContact;
        this.handler = handler;
    }
}
