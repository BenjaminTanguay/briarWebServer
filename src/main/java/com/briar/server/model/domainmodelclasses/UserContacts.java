package com.briar.server.model.domainmodelclasses;

import com.briar.server.exception.ObjectAlreadyExistsException;
import com.briar.server.exception.UserContactDoesntExistsException;

import java.util.ArrayList;
import java.util.HashMap;

public class UserContacts {

    private HashMap<String, UserContact> contacts;

    public UserContacts() {
        this.contacts = new HashMap<String, UserContact>();
    }

    public void addContact(String userName, UserContact userContact) throws ObjectAlreadyExistsException {
        if (this.contacts.containsKey(userName)) {
            throw new ObjectAlreadyExistsException();
        }
        this.contacts.put(userName, userContact);
    }

    public UserContact removeContact(String userName) throws UserContactDoesntExistsException {
        if (!this.contacts.containsKey(userName)) {
            throw new UserContactDoesntExistsException();
        }
        return this.contacts.remove(userName);
    }

    public ArrayList<String> getAllValidContacts(String userName) throws UserContactDoesntExistsException {
        if (!this.contacts.containsKey(userName)) {
            throw new UserContactDoesntExistsException();
        }
        ArrayList<String> userList = new ArrayList<String>();
        this.contacts.forEach((userAsKey, userContactAsValue) -> {
            if (userContactAsValue.isContactBilateral()) {
                userList.add(userAsKey);
            }
        });
        return userList;
    }

    public ArrayList<String> getAllContacts(String userName) throws UserContactDoesntExistsException {
        if (!this.contacts.containsKey(userName)) {
            throw new UserContactDoesntExistsException();
        }
        ArrayList<String> userList = new ArrayList<String>();
        this.contacts.forEach((userAsKey, userContactAsValue) -> {
            userList.add(userAsKey);
        });
        return userList;
    }


//    public void addUserContact(UserContact userContact) throws ObjectAlreadyExistsException {
//        User firstUser = userContact.getFirstUser();
//        User secondUser = userContact.getSecondUser();
//        addUserHelper(firstUser, userContact);
//        addUserHelper(secondUser, userContact);
//    }
//
//    private void addUserHelper(User user, UserContact userContact) throws ObjectAlreadyExistsException {
//        User otherUser = userContact.getOtherUser(user);
//        if (this.contacts.containsKey(user)) {
//            HashMap<User, UserContact> contactList = this.contacts.get(user);
//            if (contactList.containsKey(otherUser)) {
//                throw new ObjectAlreadyExistsException();
//            } else {
//
//            }
//        } else {
//            HashMap<User, UserContact> newContactList = new HashMap<User, UserContact>();
//            newContactList.put(otherUser, userContact);
//            this.contacts.put(user, newContactList);
//        }
//    }

}
