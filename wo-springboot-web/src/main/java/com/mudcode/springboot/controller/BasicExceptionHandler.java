package com.mudcode.springboot.controller;

import com.mudcode.springboot.exception.ServiceException;
import com.mudcode.springboot.message.ServiceMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestControllerAdvice
public class BasicExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(BasicExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        log.error("URI: {}, error: {} - {}", request.getDescription(false), ex.getClass().getSimpleName(),
                ex.getMessage());
        if (log.isDebugEnabled()) {
            log.debug(ex.getMessage(), ex);
        }
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handlerException(Exception ex, WebRequest request) {
        String exceptionName = ex.getClass().getSimpleName();
        String exceptionMessage = ex.getMessage();
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                exceptionName + " - " + exceptionMessage);
        return handleExceptionInternal(ex, detail, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {ServiceException.class})
    public ResponseEntity<ServiceMessage<String>> handlerServerException(ServiceException e,
                                                                         HttpServletRequest request) {
        String exceptionName = e.getClass().getSimpleName();
        int code = e.getCode();
        String uri = request.getRequestURI();
        log.error("URI: {}, error: {} - {} - {}", uri, code, exceptionName, e.getMessage());
        if (log.isDebugEnabled()) {
            log.debug(e.getMessage(), e);
        }

        int status = HttpStatus.BAD_REQUEST.value();

        ServiceMessage<String> serviceMessage = new ServiceMessage<>();
        serviceMessage.setCode(code);
        serviceMessage.setMessage(exceptionName);
        serviceMessage.setData(e.getMessage());
        return ResponseEntity.status(status).body(serviceMessage);
    }

}
