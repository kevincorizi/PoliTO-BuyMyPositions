package it.polito.gruppo2.mamk.lab3.exceptions;


public class InvalidPositionException extends RuntimeException {

    public InvalidPositionException() {
        super();
    }

    public InvalidPositionException(String message) {
        super(message);
    }

    public InvalidPositionException(Throwable reason) {
        super(reason);
    }

}
