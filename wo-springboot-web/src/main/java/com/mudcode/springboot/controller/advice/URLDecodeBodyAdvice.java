package com.mudcode.springboot.controller.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@ControllerAdvice
@RestControllerAdvice
public class URLDecodeBodyAdvice extends RequestBodyAdviceAdapter {

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasMethodAnnotation(URLDecodeBody.class)
                || methodParameter.hasParameterAnnotation(URLDecodeBody.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        MediaType requestMediaType = inputMessage.getHeaders().getContentType();
        if (MediaType.APPLICATION_FORM_URLENCODED.equalsTypeAndSubtype(requestMediaType)) {
            Charset requestCharset = requestMediaType == null ? null : requestMediaType.getCharset();
            if (requestCharset == null || !requestCharset.isRegistered()) {
                requestCharset = StandardCharsets.UTF_8;
            }
            /*
             * 仅适用特定场景，不通用。（尤其是文件上传场景不能单纯的做 流复制 的处理）
             */
            String needToDecode = StreamUtils.copyToString(inputMessage.getBody(), requestCharset);
            String decoded = URLDecoder.decode(needToDecode, requestCharset);
            return new DecodeHttpInputMessage(inputMessage.getHeaders(),
                    new ByteArrayInputStream(decoded.getBytes(requestCharset)));
        }
        return super.beforeBodyRead(inputMessage, parameter, targetType, converterType);
    }

}
