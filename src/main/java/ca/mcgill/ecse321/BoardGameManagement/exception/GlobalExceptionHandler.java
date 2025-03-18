package ca.mcgill.ecse321.BoardGameManagement.exception;

import ca.mcgill.ecse321.BoardGameManagement.dto.ErrorDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(GlobalException.class)
  public ResponseEntity<ErrorDto> handleGlobalException(GlobalException e) {
    return new ResponseEntity<ErrorDto>(new ErrorDto(e.getMessage()), e.getStatus());
  }

  @ExceptionHandler(ConstraintViolationException.class) 
  public ResponseEntity<ErrorDto> handleConstraintViolationException(ConstraintViolationException e) {
    List<String> errors = new ArrayList<>();
    for (ConstraintViolation<?> cv : e.getConstraintViolations()) {
      errors.add(cv.getMessage());
    }
    return new ResponseEntity<ErrorDto>(new ErrorDto(errors), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
    return new ResponseEntity<>(new ErrorDto("Field is missing"), HttpStatus.BAD_REQUEST);
  }
  
}
