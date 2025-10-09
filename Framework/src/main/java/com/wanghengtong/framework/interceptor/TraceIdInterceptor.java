package com.wanghengtong.framework.interceptor;

import com.wanghengtong.framework.utils.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TraceId拦截器，在每个请求中添加唯一的TraceId
 */
@Slf4j
@Component
public class TraceIdInterceptor implements HandlerInterceptor {

    private static final String TRACE_ID = "traceId";
    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        MDC.put(START_TIME, String.valueOf(startTime));

        // 尝试从请求头中获取TraceId
        String traceId = request.getHeader(TRACE_ID);

        // 如果请求头中没有，则生成新的TraceId
        if (!StringUtils.hasText(traceId)) {
            traceId = TraceIdUtil.generateTraceId();
        }

        // 将TraceId放入MDC中，以便日志框架可以获取
        TraceIdUtil.setTraceId(traceId);

        // 同时将TraceId放入响应头中，便于客户端追踪
        response.setHeader(TRACE_ID, traceId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后清理MDC中的TraceId和开始时间
        TraceIdUtil.clearTraceId();
        MDC.remove(START_TIME);
    }

}