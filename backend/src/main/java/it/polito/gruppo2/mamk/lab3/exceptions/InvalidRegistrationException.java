package it.polito.gruppo2.mamk.lab3.exceptions;


public class InvalidRegistrationException extends RuntimeException {

    public InvalidRegistrationException() {
        super("Invalid registration fields provided");
    }

    public InvalidRegistrationException(String message) {
        super(message);
    }

    public InvalidRegistrationException(Throwable reason) {
        super(reason);
    }

}
