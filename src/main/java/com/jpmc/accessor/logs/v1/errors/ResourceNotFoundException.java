package com.jpmc.accessor.logs.v1.errors;
/**
 * ResourceNotFoundException that will result in 404 HTTP status code
 */
public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException() { super(); }
    public ResourceNotFoundException(String msg) { super(msg); }
    public ResourceNotFoundException(Throwable cause) { super(cause); }
    public ResourceNotFoundException(String message, Throwable cause) { super(message, cause); }
}
