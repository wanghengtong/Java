package com.wanghengtong.oauth2clientdemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oauth2.client")
@Data
public class OAuth2ClientConfig {

    private String clientId;

    private String clientSecret;

    private String responseType;

    private String scope;

    private String grantType;

    private String getCodeUri;

    private String accessTokenUri;

    private String resourceUri;

    private String redirectUri;

}
