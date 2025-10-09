package com.wanghengtong.framework.config;

import com.wanghengtong.framework.interceptor.LicenseCheckInterceptor;
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webInterceptor);
        // registry.addInterceptor(licenseCheckInterceptor);
    }

}