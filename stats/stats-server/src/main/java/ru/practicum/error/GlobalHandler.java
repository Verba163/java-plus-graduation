package ru.practicum.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.error.exception.StatsIllegalDateTime;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice
@Slf4j
public class GlobalHandler {
    @ExceptionHandler(StatsIllegalDateTime.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleStatsIllegalDateTime(final StatsIllegalDateTime e) {
        log.warn("400 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .title("Error...")
                .message(e.getMessage())
                .stackTrace(sw.toString())
                .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        log.warn("400 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .title("Error...")
                .message(e.getMessage())
                .stackTrace(sw.toString())
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
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .title("Error...")
                .message(e.getMessage())
                .stackTrace(sw.toString())
                .build();
    }
}
