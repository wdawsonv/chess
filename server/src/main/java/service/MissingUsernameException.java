package service;

public class MissingUsernameException extends Exception {
    public MissingUsernameException(String message) {
        super(message);
    }
    public MissingUsernameException(String message, Throwable ex) {
        super(message, ex);
    }
}
