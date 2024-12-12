package com.mudcode.springboot.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
public class MDCRequestFilter extends OncePerRequestFilter {

    private static final String X_REQUEST_ID = "X-REQUEST-ID";

    public static String requestId(HttpServletRequest request) {
        return String.valueOf(request.getAttribute(X_REQUEST_ID));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        List<String> requestIds = new ArrayList<>();
        String requestId = "";
        Optional.ofNullable(request.getHeaders(X_REQUEST_ID))
                .ifPresent(headers -> headers.asIterator().forEachRemaining(requestIds::add));
        if (requestIds.isEmpty()) {
            requestId = RandomStringUtils.randomAlphabetic(16);
        } else {
            requestId = String.join(", ", requestIds);
        }
        MDC.put(X_REQUEST_ID, requestId);
        request.setAttribute(X_REQUEST_ID, requestId);
        response.addHeader(X_REQUEST_ID, requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(X_REQUEST_ID);
        }
    }

}
