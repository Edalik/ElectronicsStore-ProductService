package ru.edalik.electronics.store.product.service.config.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import ru.edalik.electronics.store.product.service.model.dto.exception.ErrorDto;
import ru.edalik.electronics.store.product.service.model.dto.exception.ValidationErrorDto;
import ru.edalik.electronics.store.product.service.model.dto.exception.ValidationErrorFieldDto;
import ru.edalik.electronics.store.product.service.model.exception.InsufficientFunds;
import ru.edalik.electronics.store.product.service.model.exception.InsufficientQuantityException;
import ru.edalik.electronics.store.product.service.model.exception.NotFoundException;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends DefaultHandlerExceptionResolver {

    @Override
    protected ModelAndView doResolveException(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler,
        Exception ex
    ) {
        log.error("Global Exception Handler: {}", ex.getMessage(), ex);
        return super.doResolveException(request, response, handler, ex);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, HandlerMethodValidationException.class})
    public ResponseEntity<ValidationErrorDto> handleException(Exception ex, HttpServletRequest request) {
        List<ValidationErrorFieldDto> fields = new ArrayList<>();
        if (ex instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            fillFields(fields, methodArgumentNotValidException);
        } else if (ex instanceof HandlerMethodValidationException handlerMethodValidationException) {
            fillFields(fields, handlerMethodValidationException);
        }

        ValidationErrorDto errorDto = ValidationErrorDto.builder()
            .timestamp(ZonedDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Error")
            .fields(fields)
            .path(request.getRequestURI())
            .build();
        log.warn("Validation Error: {}", errorDto);

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    private static void fillFields(List<ValidationErrorFieldDto> fields, MethodArgumentNotValidException ex) {
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fields.add(new ValidationErrorFieldDto(fieldError.getField(), fieldError.getDefaultMessage()));
        }
    }

    private static void fillFields(List<ValidationErrorFieldDto> fields, HandlerMethodValidationException ex) {
        for (ParameterValidationResult parameter : ex.getParameterValidationResults()) {
            for (MessageSourceResolvable error : parameter.getResolvableErrors()) {
                String parameterName = parameter.getMethodParameter().getParameterName();
                String defaultMessage = error.getDefaultMessage();
                fields.add(new ValidationErrorFieldDto(parameterName, defaultMessage));
            }
        }
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        ErrorDto errorDto = getErrorDto(HttpStatus.NOT_FOUND, ex, request);
        log.warn("Not Found: {}", errorDto);

        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = InsufficientQuantityException.class)
    public ResponseEntity<ErrorDto> handleInsufficientQuantityException(
        InsufficientQuantityException ex,
        HttpServletRequest request
    ) {
        ErrorDto errorDto = getErrorDto(HttpStatus.BAD_REQUEST, ex, request);
        log.warn("Insufficient Quantity: {}", errorDto);

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = InsufficientFunds.class)
    public ResponseEntity<ErrorDto> handleInsufficientFunds(InsufficientFunds ex, HttpServletRequest request) {
        ErrorDto errorDto = getErrorDto(HttpStatus.BAD_REQUEST, ex, request);
        log.warn("Insufficient Funds: {}", errorDto);

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    private static ErrorDto getErrorDto(HttpStatus status, Exception ex, HttpServletRequest request) {
        return ErrorDto.builder()
            .timestamp(ZonedDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
    }

}