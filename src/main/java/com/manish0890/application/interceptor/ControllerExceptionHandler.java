package com.manish0890.application.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manish0890.application.dto.ErrorSummary;
import com.manish0890.application.exception.NotFoundException;
import com.manish0890.application.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;

import javax.validation.ConstraintViolationException;
import java.util.Date;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorSummary(ex));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                .body(buildErrorSummary(ex));
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ResponseEntity<Object> handleServiceException(ServiceException ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                .body(buildErrorSummary(ex));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorSummary(ex));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(WebExchangeBindException ex) {

        String subStrKeyword = "default message [";

        ErrorSummary errorSummary = new ErrorSummary();
        errorSummary.setTimestamp(new Date().toString());
        errorSummary.setError(ex.getReason());

        if (StringUtils.isNotBlank(ex.getMessage()) && ex.getMessage().contains(subStrKeyword)) {
            String errorMessage = ex.getMessage();
            errorSummary.setMessage(errorMessage.substring(errorMessage.indexOf(subStrKeyword)));
            LOGGER.error(errorSummary.getMessage(), ex);
        } else {
            LOGGER.error(errorSummary.getError(), ex);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(getStringValue(errorSummary));
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleUncommonException(Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorSummary(ex));
    }

    private String buildErrorSummary(Exception ex) {
        ErrorSummary errorSummary = new ErrorSummary();
        errorSummary.setMessage(ex.getMessage());
        errorSummary.setTimestamp(new Date().toString());
        return getStringValue(errorSummary);
    }

    private String getStringValue(ErrorSummary errorSummary) {
        try {
            return mapper.writeValueAsString(errorSummary);
        } catch (JsonProcessingException e) {
            return errorSummary.toString();
        }
    }
}
