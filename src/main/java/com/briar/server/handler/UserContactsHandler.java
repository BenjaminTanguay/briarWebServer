package com.briar.server.handler;

import com.briar.server.constants.Constants;
import com.briar.server.exception.IncompleteObjectException;
import com.briar.server.exception.ObjectDeletedException;
import com.briar.server.exception.UserContactDoesntExistsException;
import com.briar.server.model.domainmodelclasses.UserContact;
import com.briar.server.model.domainmodelclasses.UserContacts;
import com.briar.server.patterns.identitymap.UserContactsIdentityMap;

public class UserContactsHandler implements IHandler {

    private UserContact userContact;
    private UserContactsIdentityMap map;

    public UserContactsHandler(UserContact userContact) throws IncompleteObjectException {
        this(userContact, UserContactsIdentityMap.getInstance());
    }

    // Parametrised constructor for testing purposes
    public UserContactsHandler(UserContact userContact, UserContactsIdentityMap map) throws IncompleteObjectException {
        this.userContact = userContact;
        this.map = map;
        String firstUserName = this.userContact.getFirstUserName();
        String secondUserName = this.userContact.getSecondUserName();
        if (firstUserName == null || secondUserName == null) {
            throw new IncompleteObjectException(this.userContact.toString());
        }
    }

    public boolean exists() throws ObjectDeletedException {
        // Making sure that the first contact's name exists in the identity map. Otherwise, the userContacts doesn't exist.
        String firstContactName = userContact.getFirstUserName();
        boolean firstUserHasAnEntryInIdentityMap = this.map.doesUserContactsExists(firstContactName);
        if (!firstUserHasAnEntryInIdentityMap) {
            return false;
        }
        // Making sure that the second contact's name exists in the identity map. Otherwise, the userContacts doesn't exist.
        String secondContactName = userContact.getSecondUserName();
        boolean secondUserHasAnEntryInIdentityMap = this.map.doesUserContactsExists(secondContactName);
        if (!secondUserHasAnEntryInIdentityMap) {
            return false;
        }

        // Making sure that the second user's name exists in the UserContacts of the first user.
        UserContacts firstUserContacts = this.map.getUserContacts(firstContactName, Constants.Lock.reading);
        boolean userContactExistInContactsOfFirstUser = firstUserContacts.contactExists(secondContactName);
        if (!userContactExistInContactsOfFirstUser) {
            return false;
        }
        this.map.stopReading(firstContactName);

        // Making sure that the first user's name exists in the UserContacts of the second user.
        UserContacts secondUserContacts = this.map.getUserContacts(secondContactName, Constants.Lock.reading);
        boolean userContactExistInContactsOfSecondUser = secondUserContacts.contactExists(firstContactName);
        if (!userContactExistInContactsOfSecondUser) {
            return false;
        }
        this.map.stopReading(secondContactName);

        // If we went through all this code, then the user contact exists in both's users lists of contacts
        return true;
    }

    public void add() throws ObjectDeletedException {
        String firstContactName = this.userContact.getFirstUserName();
        String secondContactName = this.userContact.getSecondUserName();

        // We get both objects for writing
        UserContacts firstUserContacts = getFirstUserContact(firstContactName);
        UserContacts secondUserContacts = getSecondUserContact(firstContactName, secondContactName);

        // We modify both objects
        firstUserContacts.addContact(secondContactName, this.userContact);
        secondUserContacts.addContact(firstContactName, this.userContact);

        // And then we send the message that we're done writing
        this.map.stopWriting(firstContactName);
        this.map.stopWriting(secondContactName);
    }

    public void remove() throws ObjectDeletedException, UserContactDoesntExistsException {
        String firstContactName = this.userContact.getFirstUserName();
        String secondContactName = this.userContact.getSecondUserName();

        // We get both objects for writing
        UserContacts firstUserContacts = getFirstUserContact(firstContactName);
        UserContacts secondUserContacts = getSecondUserContact(firstContactName, secondContactName);

        // We modify both objects. If that doesn't work, we send the msg to stop writing and we throw the error.
        // In the case the first modification worked but the second throws an error, we put back the first object the way
        // it was before.
        UserContact oldFirstUserContact;
        try {
            oldFirstUserContact = firstUserContacts.removeContact(secondContactName);
        } catch (UserContactDoesntExistsException e) {
            this.map.stopWriting(firstContactName);
            this.map.stopWriting(secondContactName);
            throw e;
        }

        try {
            secondUserContacts.removeContact(firstContactName);
        } catch (UserContactDoesntExistsException e) {
            secondUserContacts.addContact(secondContactName, oldFirstUserContact);
            this.map.stopWriting(firstContactName);
            this.map.stopWriting(secondContactName);
            throw e;
        }

        // And then we send the message that we're done writing
        this.map.stopWriting(firstContactName);
        this.map.stopWriting(secondContactName);
    }

    public void modify() throws ObjectDeletedException, UserContactDoesntExistsException {
        String firstContactName = this.userContact.getFirstUserName();
        String secondContactName = this.userContact.getSecondUserName();

        // We get both objects for writing
        UserContacts firstUserContacts = getFirstUserContact(firstContactName);
        UserContacts secondUserContacts = getSecondUserContact(firstContactName, secondContactName);

        // We get both UserContact from within the UserContacts
        UserContact firstUserContact;
        UserContact secondUserContact;
        try {
            firstUserContact = firstUserContacts.getUserContact(secondContactName);
            secondUserContact = secondUserContacts.getUserContact(firstContactName);
        } catch (UserContactDoesntExistsException e) {
            this.map.stopWriting(firstContactName);
            this.map.stopWriting(secondContactName);
            throw e;
        }

        // We copy the information found in the newly created UserContact to both UserContact s
        firstUserContact.copy(this.userContact);
        secondUserContact.copy(this.userContact);

        // And then we send the message that we're done writing
        this.map.stopWriting(firstContactName);
        this.map.stopWriting(secondContactName);
    }

    private UserContacts getFirstUserContact(String firstContactName) throws ObjectDeletedException {
        try {
            return this.map.getUserContacts(firstContactName, Constants.Lock.writing);
        } catch (ObjectDeletedException e) {
            this.map.stopWriting(firstContactName);
            throw e;
        }
    }

    private UserContacts getSecondUserContact(String firstContactName, String secondContactName) throws ObjectDeletedException {
        try {
            return this.map.getUserContacts(secondContactName, Constants.Lock.writing);
        } catch (ObjectDeletedException e) {
            this.map.stopWriting(firstContactName);
            this.map.stopWriting(secondContactName);
            throw e;
        }
    }

}
