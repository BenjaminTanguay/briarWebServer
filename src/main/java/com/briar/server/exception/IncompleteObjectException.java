package com.briar.server.exception;


public class IncompleteObjectException extends Exception {
    public IncompleteObjectException() { super("The object to be committed is lacking important parameters."); }
    public IncompleteObjectException(String message) { super("The object to be committed is lacking important parameters:" + message); }
    public IncompleteObjectException(String message, Throwable cause) { super(message, cause); }
    public IncompleteObjectException(Throwable cause) { super(cause); }
}
