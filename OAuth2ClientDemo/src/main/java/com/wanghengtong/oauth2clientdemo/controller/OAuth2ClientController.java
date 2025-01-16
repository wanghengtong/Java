package com.wanghengtong.oauth2clientdemo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanghengtong.oauth2clientdemo.config.OAuth2ClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.cert.X509Certificate;

@Slf4j
@RestController
@RequestMapping("/authorize")
public class OAuth2ClientController {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private OAuth2ClientConfig oauth2ClientConfig;

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        restTemplate = new RestTemplate();
    }

    @RequestMapping("/login")
    public String login(String code, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 实际集成开发中需要应用开发者先判断用户认证状态，这里简单实现
        if (StringUtils.isNotEmpty(code)) {
            log.info("code:{}", code);
            // 获取授权令牌
            String getTokenUrl = oauth2ClientConfig.getAccessTokenUri() +
                    "?client_id=" + oauth2ClientConfig.getClientId() +
                    "&client_secret=" + oauth2ClientConfig.getClientSecret() +
                    "&grant_type=" + oauth2ClientConfig.getGrantType() +
                    "&scope=" + oauth2ClientConfig.getScope() +
                    "&redirect_uri=" + oauth2ClientConfig.getRedirectUri() +
                    "&code=" + code;
            log.info("getTokenUrl:{}", getTokenUrl);
            String tokenResponse = restTemplate.getForObject(getTokenUrl, String.class);
            log.info("tokenResponse:{}", tokenResponse);
            JsonNode objectNode = objectMapper.readValue(tokenResponse, JsonNode.class);
            String accessToken = objectNode.get("access_token").asText();
            log.info("accessToken:{}", accessToken);

            // 获取用户详情
            String getResourceUrl = oauth2ClientConfig.getResourceUri() + "?access_token=" + accessToken;
            log.info("getResourceUrl:{}", getResourceUrl);
            String resourceResponse = restTemplate.getForObject(getResourceUrl, String.class);
            log.info("resourceResponse:{}", resourceResponse);
            // 获取到用户信息后，可以继续执行应用系统业务逻辑...
            return resourceResponse;
        } else {
            // 判断出用户未认证时，重定向到网关获取OAuth2.0授权码
            String getCodeUrl = oauth2ClientConfig.getGetCodeUri() +
                    "?client_id=" + oauth2ClientConfig.getClientId() +
                    "&response_type=" + oauth2ClientConfig.getResponseType() +
                    "&scope=" + oauth2ClientConfig.getScope() +
                    "&redirect_uri=" + oauth2ClientConfig.getRedirectUri() +
                    "&state=state";
            log.info("getCodeUrl:{}  ", getCodeUrl);
            response.setStatus(302);
            response.sendRedirect(getCodeUrl);
            return null;
        }
    }

}