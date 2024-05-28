package Ci.Backend.Exceptions;

import Ci.Backend.Dtos.InvalidDtoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(value = InvalidDtoException.class)
    public ResponseEntity<Object> handleInvalidDto()
    {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
