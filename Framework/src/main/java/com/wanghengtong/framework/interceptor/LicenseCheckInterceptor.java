package com.wanghengtong.framework.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wanghengtong.framework.factory.ObjectMapperFactory;
import com.wanghengtong.framework.license.LicenseVerify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月27日 19:00
 */
@Slf4j
@Component
public class LicenseCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LicenseVerify licenseVerify = new LicenseVerify();
        boolean verifyResult = licenseVerify.verify();
        if (verifyResult) {
            return true;
        } else {
            response.setCharacterEncoding("utf-8");
            Map<String, String> result = new HashMap<>(1);
            result.put("result", "您的证书无效，请核查服务器是否取得授权或重新申请证书！");
            try {
                response.getWriter().write(ObjectMapperFactory.getINSTANCE().getObjectMapper().writeValueAsString(result));
            } catch (JsonProcessingException e) {
                // 记录日志或抛出自定义异常
                log.error("Failed to serialize response", e);
                response.getWriter().write("{\"result\": \"服务器内部错误\"}");
            }
            return false;
        }
    }

}

