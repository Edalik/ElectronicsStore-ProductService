package ru.edalik.electronics.store.product.service.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ru.edalik.electronics.store.product.service.model.dto.exception.ErrorDto;
import ru.edalik.electronics.store.product.service.model.dto.exception.ValidationErrorDto;
import ru.edalik.electronics.store.product.service.model.dto.exception.ValidationErrorFieldDto;
import ru.edalik.electronics.store.product.service.model.exception.InsufficientQuantityException;
import ru.edalik.electronics.store.product.service.model.exception.NotFoundException;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDto> handleException(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        List<ValidationErrorFieldDto> fields = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fields.add(new ValidationErrorFieldDto(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        ValidationErrorDto errorDto = ValidationErrorDto.builder()
            .timestamp(ZonedDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Error")
            .fields(fields)
            .path(request.getRequestURI())
            .build();

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = HandlerMethodValidationException.class)
    public ResponseEntity<ValidationErrorDto> handleException(
        HandlerMethodValidationException ex,
        HttpServletRequest request
    ) {
        List<ValidationErrorFieldDto> fields = new ArrayList<>();
        for (ParameterValidationResult parameter : ex.getParameterValidationResults()) {
            for (MessageSourceResolvable error : parameter.getResolvableErrors()) {
                String parameterName = parameter.getMethodParameter().getParameterName();
                String defaultMessage = error.getDefaultMessage();
                fields.add(new ValidationErrorFieldDto(parameterName, defaultMessage));
            }
        }
        ValidationErrorDto errorDto = ValidationErrorDto.builder()
            .timestamp(ZonedDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Error")
            .fields(fields)
            .path(request.getRequestURI())
            .build();

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        ErrorDto errorDto = ErrorDto.builder()
            .timestamp(ZonedDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error(HttpStatus.NOT_FOUND.getReasonPhrase())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = InsufficientQuantityException.class)
    public ResponseEntity<ErrorDto> handleInsufficientQuantityException(
        InsufficientQuantityException ex,
        HttpServletRequest request
    ) {
        ErrorDto errorDto = ErrorDto.builder()
            .timestamp(ZonedDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

}