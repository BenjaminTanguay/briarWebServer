package com.briar.server.services.tasks;

import com.briar.server.handler.IHandler;
import com.briar.server.handler.UserHandler;
import com.briar.server.mapper.UserMapper;
import com.briar.server.model.domainmodelclasses.User;

import javax.inject.Inject;

public abstract class AbstractUserTask implements ITask {

    protected User user;
    protected IHandler handler;

    @Inject
    protected UserMapper userMapper;

    // Second parametrised constructor for testing purposes
    public AbstractUserTask(User user, UserHandler handler) {
        this.user = user;
        this.handler = handler;
    }
}
