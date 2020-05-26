package com.manish0890.application.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manish0890.application.dto.ErrorSummary;
import com.manish0890.application.exception.NotFoundException;
import com.manish0890.application.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) throws JsonProcessingException {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapper.writeValueAsString(buildErrorSummary(ex)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) throws JsonProcessingException {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                .body(mapper.writeValueAsString(buildErrorSummary(ex)));
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ResponseEntity<Object> handleServiceException(ServiceException ex) throws JsonProcessingException {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                .body(mapper.writeValueAsString(buildErrorSummary(ex)));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleUncommonException(Exception ex) throws JsonProcessingException {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapper.writeValueAsString(buildErrorSummary(ex)));
    }

    private ErrorSummary buildErrorSummary(Exception ex) {
        ErrorSummary errorSummary = new ErrorSummary();
        errorSummary.setMessage(ex.getMessage());
        errorSummary.setTimestamp(new Date().toString());
        return errorSummary;
    }
}
