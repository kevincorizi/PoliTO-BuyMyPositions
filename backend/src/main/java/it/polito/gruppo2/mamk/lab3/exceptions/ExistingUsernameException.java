package it.polito.gruppo2.mamk.lab3.exceptions;

public class ExistingUsernameException extends RuntimeException{

    public ExistingUsernameException() { super(); }

    public ExistingUsernameException(String message) { super(message); }

    public ExistingUsernameException(Throwable reason) {
        super(reason);
    }
}
