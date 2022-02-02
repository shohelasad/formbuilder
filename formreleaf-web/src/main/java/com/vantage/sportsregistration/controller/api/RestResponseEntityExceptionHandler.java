package com.vantage.sportsregistration.controller.api;

import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bazlur Rahman Rokon
 * @since 6/15/15.
 */
@ControllerAdvice(basePackages = "com.vantage.sportsregistration.controller.api")
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> globalErrors = new ArrayList<>();
        Map<String, String> fieldErrors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));

        exception.getBindingResult()
                .getGlobalErrors()
                .forEach(objectError -> globalErrors.add(objectError.getObjectName() + ", " + objectError.getDefaultMessage()));

        ErrorMessage errorMessage = new ErrorMessage(globalErrors, fieldErrors);

        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        String unsupported = "Unsupported content type: " + ex.getContentType();
        String supported = "Supported content types: " + MediaType.toString(ex.getSupportedMediaTypes());
        ErrorMessage errorMessage = new ErrorMessage(unsupported, supported);

        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        Throwable mostSpecificCause = ex.getMostSpecificCause();
        ErrorMessage errorMessage;
        if (mostSpecificCause != null) {
            String exceptionName = mostSpecificCause.getClass().getName();
            String message = mostSpecificCause.getMessage();
            errorMessage = new ErrorMessage(exceptionName, message);
        } else {
            errorMessage = new ErrorMessage(ex.getMessage());
        }

        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, OptimisticLockingFailureException.class,
            DataIntegrityViolationException.class})
    @ResponseBody
    public ResponseEntity handleConflict(Exception ex) {

        return errorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ResponseEntity<?> handleAnyException(Exception e) {

        return errorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({InvocationTargetException.class, IllegalArgumentException.class, ClassCastException.class,
            ConversionFailedException.class})
    @ResponseBody
    public ResponseEntity handleMiscFailures(Throwable t) {

        return errorResponse(t, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({FileUploadBase.SizeLimitExceededException.class, MaxUploadSizeExceededException.class, MultipartException.class})
    public ResponseEntity handleSizeLimitExceededException(Throwable t) {

        return errorResponse(t, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity errorResponse(Throwable throwable, HttpStatus status) {

        if (null != throwable) {
            LOGGER.error("error caught: " + throwable.getMessage(), throwable);
            String message = throwable.getMessage();

            return response(new ErrorMessage(message), status);
        } else {
            LOGGER.error("unknown error caught in RESTController, {}", status);
            return response(null, status);
        }
    }

    protected <T> ResponseEntity<T> response(T body, HttpStatus status) {
        LOGGER.debug("Responding with a status of {}", status);

        return new ResponseEntity<>(body, new HttpHeaders(), status);
    }
}
