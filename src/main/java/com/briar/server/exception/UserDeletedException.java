package com.briar.server.exception;

/**
 * Created by Benjamin on 2018-02-22.
 */
public class UserDeletedException extends Exception {

    public UserDeletedException() { super("The user doesn't exist anymore"); }
    public UserDeletedException(String message) { super(message); }
    public UserDeletedException(String message, Throwable cause) { super(message, cause); }
    public UserDeletedException(Throwable cause) { super(cause); }
}
