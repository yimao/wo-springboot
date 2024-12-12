package com.mudcode.springboot.controller;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.mudcode.springboot.LogUtil;
import com.mudcode.springboot.common.util.JsonUtil;
import com.mudcode.springboot.exception.ServiceException;
import com.mudcode.springboot.filter.MDCRequestFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WebHookController {

    private static final Logger logger = LogUtil.logger(WebHookController.class);

    @RequestMapping
    public ResponseEntity<WebHookItem> webhook(
            HttpServletRequest request,
            @RequestBody(required = false) String body,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "badReq", required = false) Integer badReq
    ) throws Exception {
        if ("1".equals(error)) {
            throw new RuntimeException("this is a RuntimeException");
        } else if ("2".equals(error)) {
            throw new Exception("this is a Exception");
        } else if ("3".equals(error)) {
            throw new ServiceException(3, "this is a ServiceException");
        }

        Enumeration<String> enumeration = request.getHeaderNames();
        Map<String, String> header = new HashMap<>();
        while (enumeration.hasMoreElements()) {
            String headerName = enumeration.nextElement();
            List<String> headerValues = new ArrayList<>();
            request.getHeaders(headerName).asIterator().forEachRemaining(headerValues::add);
            header.put(headerName, String.join(", ", headerValues));
        }

        WebHookItem item = WebHookItem.builder()
                .requestID(MDCRequestFilter.requestId(request))
                .timestamp(new Date())
                .nonce(RandomUtils.secure().randomInt())
                .contextPath(request.getContextPath())
                .parameter(request.getParameterMap())
                .header(header)
                .requestURL(request.getRequestURL().toString())
                .requestURI(request.getRequestURI())
                .queryString(request.getQueryString())
                .body(body)
                .build();

        if (logger.isDebugEnabled()) {
            String debugLog = JsonUtil.toJson(item);
            logger.debug("WebHookItem: {}", debugLog);
        }

        return ResponseEntity.ok(item);
    }

    @JacksonXmlRootElement(localName = "Webhook")
    @Builder
    @Data
    public static class WebHookItem {

        private String requestID;

        private String requestURL;

        private String contextPath;

        private String requestURI;

        private String queryString;

        private String body;

        private Map<String, String[]> parameter;

        private Map<String, String> header;

        private Date timestamp;

        private int nonce;

    }

}
