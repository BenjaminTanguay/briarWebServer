package com.briar.server.model;

import com.briar.server.exception.UserContactAlreadyExistsException;
import com.briar.server.exception.UserContactDoesntExistsException;

import java.util.ArrayList;
import java.util.HashMap;

public class UserContacts {

    private HashMap<User, UserContact> contacts;

    public UserContacts() {
        // HashMap<User, UserContact> contactList = new HashMap<User, UserContact>();
        this.contacts = new HashMap<User, UserContact>();
    }

    public void addContact(User user, UserContact userContact) throws UserContactAlreadyExistsException {
        if (this.contacts.containsKey(user)) {
            throw new UserContactAlreadyExistsException();
        }
        this.contacts.put(user, userContact);
    }

    public UserContact removeContact(User user) throws UserContactDoesntExistsException {
        if (!this.contacts.containsKey(user)) {
            throw new UserContactDoesntExistsException();
        }
        return this.contacts.remove(user);
    }

    public ArrayList<User> getAllValidContacts(User user) throws UserContactDoesntExistsException {
        if (!this.contacts.containsKey(user)) {
            throw new UserContactDoesntExistsException();
        }
        ArrayList<User> userList = new ArrayList<User>();
        this.contacts.forEach((userAsKey, userContactAsValue) -> {
            if (userContactAsValue.isContactBilateral()) {
                userList.add(userAsKey);
            }
        });
        return userList;
    }

    public ArrayList<User> getAllContacts(User user) throws UserContactDoesntExistsException {
        if (!this.contacts.containsKey(user)) {
            throw new UserContactDoesntExistsException();
        }
        ArrayList<User> userList = new ArrayList<User>();
        this.contacts.forEach((userAsKey, userContactAsValue) -> {
            userList.add(userAsKey);
        });
        return userList;
    }


//    public void addUserContact(UserContact userContact) throws UserContactAlreadyExistsException {
//        User firstUser = userContact.getFirstUser();
//        User secondUser = userContact.getSecondUser();
//        addUserHelper(firstUser, userContact);
//        addUserHelper(secondUser, userContact);
//    }
//
//    private void addUserHelper(User user, UserContact userContact) throws UserContactAlreadyExistsException {
//        User otherUser = userContact.getOtherUser(user);
//        if (this.contacts.containsKey(user)) {
//            HashMap<User, UserContact> contactList = this.contacts.get(user);
//            if (contactList.containsKey(otherUser)) {
//                throw new UserContactAlreadyExistsException();
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
