package com.briar.server.exception;

public class UserContactAlreadyExistsException extends Exception {

    public UserContactAlreadyExistsException() { super("The user contact you're trying to create already exists!"); }
    public UserContactAlreadyExistsException(String message) { super(message); }
    public UserContactAlreadyExistsException(String message, Throwable cause) { super(message, cause); }
    public UserContactAlreadyExistsException(Throwable cause) { super(cause); }
}
