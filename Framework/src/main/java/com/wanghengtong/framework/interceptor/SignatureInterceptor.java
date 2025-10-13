package com.wanghengtong.framework.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanghengtong.framework.common.ApiResponse;
import com.wanghengtong.framework.config.SignatureConfig;
import com.wanghengtong.framework.enums.HttpCodeEnum;
import com.wanghengtong.framework.utils.SignatureUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 请求验签拦截器
 *
 * @author wanghengtong
 */
@Slf4j
@Component
public class SignatureInterceptor implements HandlerInterceptor {

    @Autowired
    private SignatureConfig signatureConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断是否启用签名验证
        if (!signatureConfig.isEnabled()) {
            return true;
        }

        // 获取请求参数
        Map<String, String[]> parameterMap = request.getParameterMap();

        // 获取签名
        String sign = request.getParameter(signatureConfig.getSignParam());
        if (!StringUtils.hasText(sign)) {
            log.warn("缺少签名参数");
            writeResponse(response, ApiResponse.error(HttpCodeEnum.RC400.getCode(), "缺少签名参数"));
            return false;
        }

        // 获取时间戳
        String timestampStr = request.getParameter(signatureConfig.getTimestampParam());
        if (!StringUtils.hasText(timestampStr)) {
            log.warn("缺少时间戳参数");
            writeResponse(response, ApiResponse.error(HttpCodeEnum.RC400.getCode(), "缺少时间戳参数"));
            return false;
        }

        // 验证时间戳有效性
        try {
            long timestamp = Long.parseLong(timestampStr);
            long currentTime = System.currentTimeMillis();
            if (Math.abs(currentTime - timestamp) > signatureConfig.getTimestampValidity()) {
                log.warn("时间戳已过期，timestamp: {}", timestamp);
                writeResponse(response, ApiResponse.error(HttpCodeEnum.RC400.getCode(), "时间戳已过期"));
                return false;
            }
        } catch (NumberFormatException e) {
            log.warn("时间戳格式错误，timestamp: {}", timestampStr);
            writeResponse(response, ApiResponse.error(HttpCodeEnum.RC400.getCode(), "时间戳格式错误"));
            return false;
        }

        // 获取随机字符串
        String nonce = request.getParameter(signatureConfig.getNonceParam());
        if (!StringUtils.hasText(nonce)) {
            log.warn("缺少随机字符串参数");
            writeResponse(response, ApiResponse.error(HttpCodeEnum.RC400.getCode(), "缺少随机字符串参数"));
            return false;
        }

        // 获取请求体（仅POST请求有内容）
        String body = "";
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            body = SignatureUtil.getBodyString(request);
        }

        // 根据配置选择签名算法并验证签名
        boolean signValid = false;
        if ("hmac-sha256".equalsIgnoreCase(signatureConfig.getAlgorithm())) {
            // 使用HMAC-SHA256算法验证签名
            signValid = SignatureUtil.verifyHmacSha256Signature(parameterMap, body, signatureConfig.getSecretKey(), sign, signatureConfig.getExcludeParams());
        } else {
            // 默认使用MD5算法验证签名
            signValid = SignatureUtil.verifySignature(parameterMap, body, signatureConfig.getSecretKey(), sign, signatureConfig.getExcludeParams());
        }

        if (!signValid) {
            log.error("签名验证失败，sign: {}", sign);
            writeResponse(response, ApiResponse.error(HttpCodeEnum.RC400.getCode(), "签名验证失败"));
            return false;
        }

        log.info("签名验证通过，sign: {}", sign);
        return true;
    }

    /**
     * 写入响应
     *
     * @param response    HttpServletResponse
     * @param apiResponse ApiResponse
     */
    private void writeResponse(HttpServletResponse response, ApiResponse<?> apiResponse) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(apiResponse));
        writer.flush();
        writer.close();
    }

}