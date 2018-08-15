package it.polito.gruppo2.mamk.lab3.exceptions;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.bson.json.JsonParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalResponseExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            ArchiveNotFoundException.class, BadRequestException.class, ExistingUsernameException.class,
            InvalidPositionException.class, InvalidRegistrationException.class, JsonParseException.class,
            InvalidDefinitionException.class, JsonMappingException.class, HttpMessageConversionException.class
    })
    protected ResponseEntity<Object> handle(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        HttpStatus responseStatus;
        if(ex instanceof ArchiveNotFoundException){
            responseStatus = HttpStatus.NOT_FOUND;
        }else if (ex instanceof BadRequestException
                || ex instanceof InvalidPositionException
                || ex instanceof InvalidRegistrationException){
            responseStatus = HttpStatus.BAD_REQUEST;
        }else if (ex instanceof ExistingUsernameException){
            responseStatus = HttpStatus.CONFLICT;
        }else if (ex instanceof HttpMessageConversionException){
            responseStatus = HttpStatus.BAD_REQUEST;
            if(ex.getMessage().contains("It is not allowed to post a position in the future!"))
                bodyOfResponse = "It is not allowed to post a position in the future!";
            else if (ex.getMessage().contains("Invalid coordinates!"))
                bodyOfResponse = "Invalid coordinates!";
        }else {
            bodyOfResponse = ex.getClass().toString() + "\n" + ex.getMessage();
            responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), responseStatus, request);
    }




}