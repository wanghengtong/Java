package com.wanghengtong.framework.aop;

import cn.hutool.core.io.IoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @author wanghengtong
 * @desc RequestBodyHandlerAdvice
 * @date 2024年12月26日 20:14
 */
@Slf4j
@RestControllerAdvice
public class RequestBodyHandlerAdvice implements RequestBodyAdvice {

    /**
     * @param methodParameter
     * @param targetType
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        log.debug("supports called for method: {}", methodParameter.getMethod().getName());
        return true;
    }

    /**
     * @param inputMessage
     * @param parameter
     * @param targetType
     * @param converterType
     * @return
     * @throws IOException
     */
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        log.debug("beforeBodyRead called for method: {}", parameter.getMethod().getName());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
        }
        InputStream inputStream = inputMessage.getBody();
        String requestBody = IoUtil.read(inputStream, StandardCharsets.UTF_8);
        log.info("Request Body: {}", new ObjectMapper().readTree(requestBody));

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
        return new HttpInputMessage() {
            @Override
            public InputStream getBody() throws IOException {
                return byteArrayInputStream;
            }

            @Override
            public HttpHeaders getHeaders() {
                return inputMessage.getHeaders();
            }
        };
    }

    /**
     * @param body
     * @param inputMessage
     * @param parameter
     * @param targetType
     * @param converterType
     * @return
     */
    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        log.debug("afterBodyRead called for method: {}", parameter.getMethod().getName());
        return body;
    }

    /**
     * @param body
     * @param inputMessage
     * @param parameter
     * @param targetType
     * @param converterType
     * @return
     */
    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        log.debug("handleEmptyBody called for method: {}", parameter.getMethod().getName());
        return null;
    }

}