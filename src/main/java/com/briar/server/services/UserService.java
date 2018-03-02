package com.briar.server.services;

import com.briar.server.model.domainmodelclasses.User;
import com.briar.server.model.returnedtobriarclasses.BriarUser;

public class UserService {

    public static BriarUser convertUserToBriarUser(User user) {
        return new BriarUser(user.getPhoneGeneratedId(), user.getIp(), user.getPort());
    }

    public static boolean validateUserParams(User user) {
        return user.getPhoneGeneratedId() != null &&
                user.getIp() != null &&
                user.getPort() != 0 &&
                user.getPassword() != null;
    }

    public static boolean authenticate(User user1, User user2) {
        return user1.getPassword().equals(user2.getPassword());
    }
}
