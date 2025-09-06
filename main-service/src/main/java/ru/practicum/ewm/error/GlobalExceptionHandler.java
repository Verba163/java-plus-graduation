package ru.practicum.ewm.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.error.exception.*;
import ru.practicum.ewm.error.exception.IllegalArgumentException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationExceptions(final ValidationException e) {
        log.warn("400 {}", e.getMessage(), e);

        return ApiError.builder()
                .message("BAD_REQUEST")
                .reason("Incorrectly made request.")
                .status(HttpStatus.BAD_REQUEST.toString())
                .errors(List.of(e.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError methodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("400 {}", e.getMessage(), e);

        List<String> errorMessages = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return ApiError.builder()
                .message("BAD_REQUEST")
                .reason("MethodArgumentNotValidException")
                .status(HttpStatus.BAD_REQUEST.toString())
                .errors(errorMessages)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationExceptionException(final ConstraintViolationException e) {
        log.warn("400 {}", e.getMessage(), e);

        List<String> errors = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        return ApiError.builder()
                .message("BAD_REQUEST")
                .reason("Constraint violation")
                .status(HttpStatus.BAD_REQUEST.toString())
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(TransactionSystemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleTransactionSystemException(final TransactionSystemException e) {
        log.warn("400 {}", e.getMessage(), e);

        return ApiError.builder()
                .message(e.getMessage())
                .reason("Constraint violation")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        log.warn("400 {}", e.getMessage());

        return ApiError.builder()
                .message(e.getMessage())
                .reason("Constraint violation")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(AccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleAccessException(final AccessException e) {
        log.warn("403 {}", e.getMessage(), e);

        return ApiError.builder()
                .message("FORBIDDEN")
                .reason("ACCESS DENIED")
                .status(HttpStatus.FORBIDDEN.toString())
                .errors(List.of(e.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.warn("404 {}", e.getMessage(), e);
        return ApiError.builder()
                .message("NOT_FOUND")
                .reason("The required object was not found.")
                .status(HttpStatus.NOT_FOUND.toString())
                .errors(List.of(e.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.warn("409 {}", e.getMessage(), e);

        return ApiError.builder()
                .message("CONFLICT")
                .reason("Integrity constraint has been violated.")
                .status(HttpStatus.CONFLICT.toString())
                .errors(List.of(e.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        log.warn("409 {}", e.getMessage(), e);

        return ApiError.builder()
                .message("CONFLICT")
                .reason("Integrity constraint has been violated.")
                .status(HttpStatus.CONFLICT.toString())
                .errors(List.of(e.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgumentException(final IllegalArgumentException e) {
        log.warn("400 {}", e.getMessage(), e);

        return ApiError.builder()
                .message("BAD_REQUEST")
                .reason("Incorrectly made request.")
                .status(HttpStatus.BAD_REQUEST.toString())
                .errors(List.of(e.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleAnyException(final Exception e) {
        log.warn("500 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        return ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .stackTrace(sw.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }
}