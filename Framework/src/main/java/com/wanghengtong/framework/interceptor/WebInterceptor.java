package com.wanghengtong.framework.interceptor;

import com.wanghengtong.framework.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Enumeration;

/**
 * @author wanghengtong
 * @desc WebInterceptor
 * @date 2024年12月26日 21:14
 */
@Slf4j
@Component
public class WebInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                log.info("Request Header - {}: {}", headerName, headerValue);
            }
        }
        // 打印完整的请求URL（包括查询参数）
        StringBuilder fullUrl = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            fullUrl.append("?").append(queryString);
        }
        log.info("Request URL: {}", fullUrl);
        log.info("Request IP Address: {}", IpUtils.getClientIpAddress(request));
        log.info("Request Time: {}", LocalDateTime.now());

        Enumeration<String> parameterNames = request.getParameterNames();
        if (parameterNames != null) {
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String paramValue = request.getParameter(paramName);
                log.info("Request Parameter - {}: {}", paramName, paramValue);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 可以在这里添加后处理逻辑
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 可以在这里添加完成后的逻辑
    }

}