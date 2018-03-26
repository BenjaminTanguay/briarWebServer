package com.briar.server.services;

import com.briar.server.constants.Constants;
import com.briar.server.exception.ObjectDeletedException;
import com.briar.server.exception.UserContactDoesntExistsException;
import com.briar.server.mapper.UserContactMapper;
import com.briar.server.mapper.UserMapper;
import com.briar.server.model.domainmodelclasses.User;
import com.briar.server.model.domainmodelclasses.UserContact;
import com.briar.server.model.domainmodelclasses.UserContacts;
import com.briar.server.patterns.identitymap.UserContactsIdentityMap;
import com.briar.server.patterns.identitymap.UserIdentityMap;
import com.briar.server.patterns.unitofwork.UnitOfWork;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserContactServiceTest {
    private User user;
    private UnitOfWork unitOfWork;
    private UserContactsIdentityMap userContactsIdentityMap;
    private UserContactMapper userContactMapper;
    private UserService userService;
    private String userName;
    private UserContact user1;
    private UserContact user2;
    private UserContact otherUser1;
    private UserContact otherUser2;

    @Before
    public void setup() {
        this.userContactMapper = mock(UserContactMapper.class);
        this.userContactsIdentityMap = UserContactsIdentityMap.getTestInstance();
        this.unitOfWork = mock(UnitOfWork.class);
        long id = 123;
        String phoneGeneratedId = "hello";
        this.userName = phoneGeneratedId;
        String password = "qwerty";
        String ipAddress = "123.123.123.123";
        int portNumber = 1234;
        int statusId = 2;
        int avatarId = 12;
        this.user = new User(id, phoneGeneratedId, password, ipAddress,
                portNumber, statusId, avatarId);
        this.userService = mock(UserService.class);

        user1 = new UserContact(1, phoneGeneratedId, id, true, "second user 1", 111, true);
        user2 = new UserContact(2, phoneGeneratedId, id, false, "second user 2", 222, true);
        otherUser1 = new UserContact(3, "second user 3", 333, true, phoneGeneratedId, id, true);
        otherUser2 = new UserContact(4, "second user 4", 444, false, phoneGeneratedId, id, true);

        List<UserContact> contactList = new ArrayList<>();
        contactList.add(user1);
        contactList.add(user2);
        contactList.add(otherUser1);
        contactList.add(otherUser2);

        try {
            when(userService.readUser(userName)).thenReturn(user);
            when(userContactMapper.findContacts(id)).thenReturn(contactList);
        } catch (ObjectDeletedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test() throws ObjectDeletedException, UserContactDoesntExistsException {
        UserContacts userContacts1 = this.userContactsIdentityMap.getUserContacts(user.getPhoneGeneratedId(), Constants.Lock.reading);
        UserContacts userContacts2 = this.userContactsIdentityMap.getUserContacts("second user 1", Constants.Lock.reading);
        UserContacts userContacts3 = this.userContactsIdentityMap.getUserContacts("second user 2", Constants.Lock.reading);
        UserContacts userContacts4 = this.userContactsIdentityMap.getUserContacts("second user 3", Constants.Lock.reading);
        UserContacts userContacts5 = this.userContactsIdentityMap.getUserContacts("second user 4", Constants.Lock.reading);

        Assert.assertEquals(user1, userContacts1.getUserContact("second user 1"));
        Assert.assertEquals(user2, userContacts1.getUserContact("second user 2"));
        Assert.assertEquals(otherUser1, userContacts1.getUserContact("second user 3"));
        Assert.assertEquals(otherUser2, userContacts1.getUserContact("second user 4"));

        Assert.assertEquals(user1, userContacts2.getUserContact(user.getPhoneGeneratedId()));
        Assert.assertEquals(user2, userContacts3.getUserContact(user.getPhoneGeneratedId()));
        Assert.assertEquals(otherUser1, userContacts4.getUserContact(user.getPhoneGeneratedId()));
        Assert.assertEquals(otherUser2, userContacts5.getUserContact(user.getPhoneGeneratedId()));
    }
}
