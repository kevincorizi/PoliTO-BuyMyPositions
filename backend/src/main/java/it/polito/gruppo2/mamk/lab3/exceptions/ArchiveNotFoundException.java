package it.polito.gruppo2.mamk.lab3.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class ArchiveNotFoundException extends RuntimeException {

    public ArchiveNotFoundException() {
    }

    public ArchiveNotFoundException(String message) {
        super(message);
    }

    public ArchiveNotFoundException(Throwable reason) {
        super(reason);
    }

}
