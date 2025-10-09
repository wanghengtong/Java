package com.wanghengtong.framework.utils;

import org.slf4j.MDC;
import java.util.UUID;

/**
 * TraceId工具类
 */
public class TraceIdUtil {
    
    private static final String TRACE_ID = "traceId";
    
    /**
     * 生成TraceId
     * @return traceId
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 设置TraceId到MDC
     * @param traceId traceId
     */
    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID, traceId);
    }
    
    /**
     * 从MDC获取TraceId
     * @return traceId
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }
    
    /**
     * 清除MDC中的TraceId
     */
    public static void clearTraceId() {
        MDC.remove(TRACE_ID);
    }

}