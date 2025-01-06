package com.wanghengtong.framework.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wanghengtong.framework.common.ApiResponse;
import com.wanghengtong.framework.factory.ObjectMapperFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author wanghengtong
 * @desc ResponseBodyHandlerAdvice
 * @date 2024年12月26日 20:15
 */
@Slf4j
@RestControllerAdvice
public class ResponseBodyHandlerAdvice implements ResponseBodyAdvice {


    /**
     * @param returnType
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    /**
     * @param body
     * @param returnType
     * @param selectedContentType
     * @param selectedConverterType
     * @param request
     * @param response
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        Object result = null;
        try {
            if (body instanceof ApiResponse<?>) {
                result = body;
            } else {
                if (body instanceof String) {
                    result = ObjectMapperFactory.getINSTANCE().getObjectMapper().writeValueAsString(ApiResponse.success(body));
                } else {
                    result = ApiResponse.success(body);
                }
            }
            log.info("Response Body: {}", ObjectMapperFactory.getINSTANCE().getObjectMapper().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            log.info("Response Body: {}", body);
        }
        log.info("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+\n");
        return result;
    }

}