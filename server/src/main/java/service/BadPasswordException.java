package service;

public class BadPasswordException extends Exception {
    public BadPasswordException(String message) {
        super(message);
    }
    public BadPasswordException(String message, Throwable ex) {
        super(message, ex);
    }
}
