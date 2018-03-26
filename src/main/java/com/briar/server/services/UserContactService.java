package com.briar.server.services;

import com.briar.server.constants.Constants;
import com.briar.server.exception.DataCompromisedException;
import com.briar.server.exception.ObjectDeletedException;
import com.briar.server.exception.UserContactDoesntExistsException;
import com.briar.server.handler.UserContactHandler;
import com.briar.server.mapper.UserContactMapper;
import com.briar.server.mapper.UserMapper;
import com.briar.server.model.domainmodelclasses.User;
import com.briar.server.model.domainmodelclasses.UserContact;
import com.briar.server.model.domainmodelclasses.UserContacts;
import com.briar.server.model.request.AddContactRequest;
import com.briar.server.patterns.identitymap.UserContactsIdentityMap;
import com.briar.server.patterns.unitofwork.UnitOfWork;
import com.briar.server.services.tasks.DeleteUserContact;
import com.briar.server.services.tasks.InsertNewUserContact;
import com.briar.server.services.tasks.ModifyUserContact;

import java.util.List;

public class UserContactService extends AbstractService<UserContact> {

    private UserContactsIdentityMap userContactMap;
    private UserService userService;
    private UserContactMapper userContactMapper;

    public UserContactService(UserContactMapper userContactMapper, UserMapper userMapper) {
        this(UserContactsIdentityMap.getInstance(), UnitOfWork.getInstance(), new UserService(userMapper), userContactMapper);
    }

    // Parametrised constructor for testing purposes
    public UserContactService(UserContactsIdentityMap userContactMap, UnitOfWork unitOfWork, UserService userService, UserContactMapper userContactMapper) {
        super(unitOfWork);
        this.userContactMap = userContactMap;
        this.userService = userService;
        this.userContactMapper = userContactMapper;
    }

    public boolean validateContactRequest(AddContactRequest request) {
        return request.getPassword() != "" && request.getPassword() != null
                && request.getTargetPhoneGeneratedId() != "" && request.getTargetPhoneGeneratedId() != null;
    }

    public boolean doesUserContactExists(String userName, String contactName) {
        UserContact userContact = null;
        try {
            userContact = readUserContact(userName, contactName);
        } catch (ObjectDeletedException e) {
            // We don't need to do anything. The userContact doesn't exist anymore. Value above is null so the method
            // will return false
        }
        return userContact != null;
    }

    /**
     * For reading purposes only. If you need to modify the UserContact object in the map, use another method.
     * Returns a clone of the UserContact found in the identity map. Modifying the clone is safe.
     *
     * @param userName
     * @param contactName
     * @return
     * @throws ObjectDeletedException
     */
    public UserContact readUserContact(String userName, String contactName) throws ObjectDeletedException {
        User user = this.userService.readUser(userName);

        // Does the list of contacts exists in the map
        boolean userContactsExistsInMap = this.userContactMap.doesUserContactsExists(userName);
        // Does the specific contact we're looking for exists in the map (the boolean here doesn't give the whole picture yet)
        boolean userContactExistsInMap = false;
        // Do we hit this specific exception while trying to find the contact
        boolean isObjectDeletedExceptionTriggered = false;
        ObjectDeletedException exception = null;

        UserContact returnValue = null;
        UserContacts userContacts;
        if (userContactsExistsInMap) {
            try {
                // We try to get the userContacts (hopefully they weren't deleted since we checked)
                userContacts = this.userContactMap.getUserContacts(userName, Constants.Lock.reading);
                // We check that the contact we're looking for exists
                userContactExistsInMap = userContacts != null || !userContacts.isEmpty()
                        || userContacts.contactExists(contactName);
                this.userContactMap.stopReading(userName);

                // Catches the edge case where the user contacts were deleted right after we checked if it existed
            } catch (ObjectDeletedException e) {
                // Shit. This means the userContacts were deleted since we last checked in the if statement.
                // The ObjectDeletedException exception occurs before the reading lock engage so we don't need to
                // "stop reading"
                isObjectDeletedExceptionTriggered = true;

                // We may need to throw the exception later. We'll keep it in memory for now
                exception = e;
            }
        }

        // We get in here if we didn't get in the if statement above or if we got into it but hit the
        // ObjectDeletedException exception
        if (!userContactsExistsInMap || isObjectDeletedExceptionTriggered) {
            // If the userContact doesn't exist in the map, we try to repopulate the map from the db first
            List<UserContact> userContactList = this.userContactMapper.findContacts(user.getId());
            handleUserContactsFromList(userContactList);

            // Then we check that the UserContact exists.
            userContacts = this.userContactMap.getUserContacts(userName, Constants.Lock.reading);
            userContactExistsInMap = userContacts != null || !userContacts.isEmpty()
                    || userContacts.contactExists(contactName);
            this.userContactMap.stopReading(userName);
        }

        if (userContactExistsInMap) {
            // Ok, here we fetch the UserContact that know for sure exists
            try {
                userContacts = this.userContactMap.getUserContacts(userName, Constants.Lock.reading);
                returnValue = userContacts.getUserContact(contactName).clone();
            } catch (UserContactDoesntExistsException e) {
                // It should exist, we just checked the step above.
            } finally {
                this.userContactMap.stopReading(userName);
            }
        } else if (isObjectDeletedExceptionTriggered) {
            // If the userContact doesn't exist in the map but we've hit the exception earlier, we need to throw it.
            throw exception;
        } else {
            // The userContact really doesn't exist. We return null.
            returnValue = null;
        }
        return returnValue;
    }

    /**
     * To add a user contact to the DB and the IdentityMap
     * @param userContact
     * @throws DataCompromisedException
     */
    public void addUserContact(UserContact userContact) throws DataCompromisedException {
        UserContactHandler handler = new UserContactHandler(userContact);
        InsertNewUserContact insertionTask = new InsertNewUserContact(userContact, handler, this.userContactMapper);
        this.commitAndPush(userContact, insertionTask);
    }

    /**
     * To modify a user contact in the DB and the IdentityMap
     * @param userContact
     * @throws ObjectDeletedException
     * @throws DataCompromisedException
     */
    public void modifyUserContact(UserContact userContact) throws ObjectDeletedException, DataCompromisedException {
        String userName = userContact.getFirstUserName();
        String contactName = userContact.getSecondUserName();
        UserContact oldUserContact = readUserContact(userName, contactName);
        UserContactHandler handler = new UserContactHandler(userContact);
        UserContactHandler oldHandler = new UserContactHandler(oldUserContact);
        ModifyUserContact modifyTask = new ModifyUserContact(userContact, oldUserContact, handler, oldHandler, this.userContactMapper);
        this.commitAndPush(userContact, modifyTask);
    }

    /**
     * To delete a user contact from the DB and the IdentityMap
     * @param userContact
     * @throws DataCompromisedException
     */
    public void removeUserContact(UserContact userContact) throws DataCompromisedException {
        UserContactHandler handler = new UserContactHandler(userContact);
        DeleteUserContact removalTask = new DeleteUserContact(userContact, handler, userContactMapper);
        this.commitAndPush(userContact, removalTask);
    }

    // Takes a list of UserContact and add them one at a time to the UserContacts of the relevant parties

    // devrait fonctionner mais List<UserContact> != UserContacts
    // TODO: 26/03/2018 Replace with addUsersToIndentityMap()
    private void handleUserContactsFromList(List<UserContact> userContactList) {
        for (UserContact contact : userContactList) {
            UserContactHandler handler = new UserContactHandler(contact, this.userContactMap);
            try {
                handler.add();
            } catch (ObjectDeletedException e) {
                // Silently fail (we want the loop to keep going)
            }
        }
    }

    public List<UserContact> updateUserIdentityMapWithDB(String userName) throws ObjectDeletedException, UserContactDoesntExistsException{
        //Retrieve user
        User user = this.userService.readUser(userName);

        //Retrieve userList from database
        List<UserContact> userList = this.userContactMapper.findContacts(user.getId());

        //Retrieve Identity Map of User
        // Does the list of contacts exists in the map
        boolean userContactsExistsInMap = this.userContactMap.doesUserContactsExists(userName);

        //Compare userList with identity map to see which is missing
        //If all of userList is in Identity Map, no need to update

        UserContacts userContacts;
        if (userContactsExistsInMap){
            userContacts = this.userContactMap.getUserContacts(userName, Constants.Lock.writing);
        }
        else{
            //Create identity map for contacts with username
            userContacts = new UserContacts();
            this.userContactMap.addUserContacts(userName, userContacts);
        }
        //Retrieved Identity Map for current user

        for(UserContact contact : userList){
            //Checks if user dosen't exists withing identity map of current user

            /////////////////////////////////////////////////////////////////////////////////////////////
            //Update cuurent user's identoty map with up-to-date contacts from database
            /////////////////////////////////////////////////////////////////////////////////////////////
            String otherUserName = contact.getOtherUser(userName);

            addDBContactToIdentityMap(otherUserName, userContacts, contact);
            /////////////////////////////////////////////////////////////////////////////////////////////
            //Updating current user contacts identity map with current user info to facilitate bilateral connection
            /////////////////////////////////////////////////////////////////////////////////////////////
            boolean otherUserContactsExistsInMap = this.userContactMap.doesUserContactsExists(otherUserName);

            if(otherUserContactsExistsInMap){
                UserContacts otherContacts = this.userContactMap.getUserContacts(otherUserName, Constants.Lock.writing);

                addDBContactToIdentityMap(userName, userContacts, contact);
                this.userContactMap.stopWriting(otherUserName);
            }
            else{
                UserContacts newUserContacts = new UserContacts();

                newUserContacts.addContact(userName, contact);
            }
        }
        this.userContactMap.stopWriting(userName);

        return userList;
    }

    private void addDBContactToIdentityMap(String userName, UserContacts userContacts, UserContact contact) throws UserContactDoesntExistsException{
        boolean doesUserNameExistInUserContacts = userContacts.contactExists(userName);
        if(!doesUserNameExistInUserContacts){
            userContacts.addContact(userName, contact);
        }
        else{
            UserContact actualContact = userContacts.getUserContact(userName);
            //Update actual contact with database info
            actualContact.copy(contact);
        }
    }
}
