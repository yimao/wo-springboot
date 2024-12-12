package com.mudcode.springboot.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @see org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController
 */
@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
public class BasicErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(BasicErrorController.class);

    @RequestMapping
    public ResponseEntity<String> error(HttpServletRequest request, HttpServletResponse response) {
        int status = response.getStatus();

        String message = "None";
        try {
            message = HttpStatus.valueOf(status).getReasonPhrase();
        } catch (Exception e) {
            logger.error("URI: {}, error: {} - {}", request.getRequestURI(), e.getClass().getSimpleName(),
                    e.getMessage(), e);
        }

        return ResponseEntity.status(status).contentType(MediaType.TEXT_PLAIN).body(status + " - " + message);
    }

}
