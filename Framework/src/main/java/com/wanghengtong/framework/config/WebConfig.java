package com.wanghengtong.framework.config;

import com.wanghengtong.framework.interceptor.LicenseCheckInterceptor;
import com.wanghengtong.framework.interceptor.SignatureInterceptor;
import com.wanghengtong.framework.interceptor.TraceIdInterceptor;
import com.wanghengtong.framework.interceptor.WebInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wanghengtong
 * @desc WebConfig
 * @date 2024年12月26日 21:15
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private WebInterceptor webInterceptor;

    @Autowired
    private LicenseCheckInterceptor licenseCheckInterceptor;
    
    @Autowired
    private TraceIdInterceptor traceIdInterceptor;
    
    @Autowired
    private SignatureInterceptor signatureInterceptor;
    
    @Autowired
    private SignatureConfig signatureConfig;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(traceIdInterceptor);
        registry.addInterceptor(webInterceptor);
        // 根据配置决定是否添加签名拦截器及拦截路径
        if (signatureConfig.isEnabled()) {
            registry.addInterceptor(signatureInterceptor)
                    .addPathPatterns(signatureConfig.getIncludePatterns())
                    .excludePathPatterns(signatureConfig.getExcludePatterns());
        }
        // registry.addInterceptor(licenseCheckInterceptor);
    }

}