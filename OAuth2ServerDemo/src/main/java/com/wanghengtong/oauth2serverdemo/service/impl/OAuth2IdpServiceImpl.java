package com.wanghengtong.oauth2serverdemo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wanghengtong.oauth2serverdemo.service.IOAuth2IdpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

/**
 * @author wanghengtong
 * @desc
 * @date 2025年04月27日 20:47
 */
@Slf4j
@Service
public class OAuth2IdpServiceImpl implements IOAuth2IdpService {

    private static HttpHeaders headers = new HttpHeaders();

    static {
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-store");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
    }

    /**
     * 获取OAuth授权码
     *
     * @param request
     * @param response
     */
    @Override
    public RedirectView authorize(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("OAuth2.0请求: {}", request.getRequestURL().toString() + "?" + request.getQueryString());
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
            String client_id = oauthRequest.getParam(OAuth.OAUTH_CLIENT_ID);
            String response_type = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
            String redirect_uri = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);
            log.info("redirect_uri===========>{}", redirect_uri);
            String state = oauthRequest.getParam(OAuth.OAUTH_STATE);
            String scope = oauthRequest.getParam(OAuth.OAUTH_SCOPE);
            String service = request.getParameter("service");

            String targetUrl = redirect_uri + "?code=" + UUID.randomUUID();

            RedirectView target = new RedirectView();
            target.setContextRelative(true);
            target.setUrl(targetUrl);
            return target;
        } catch (OAuthProblemException | OAuthSystemException e) {
            log.error(e.getMessage(), e);
            PrintWriter out = null;
            try {
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                out = response.getWriter();
                out.print("<script>alert('" + e.getMessage() + "')</script>");
                out.print("<script>window.close();</script>");
            } catch (IOException io) {
                throw new RuntimeException(io.getMessage());
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }
        return null;
    }

    /**
     * 获取OAuth访问令牌
     *
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<String> accessToken(HttpServletRequest request) {
        ObjectNode oauthAccessToken = new ObjectMapper().createObjectNode();
        oauthAccessToken.put("access_token", String.valueOf(UUID.randomUUID()));
        oauthAccessToken.put("expires_in", 600);
        oauthAccessToken.put("refresh_token", String.valueOf(UUID.randomUUID()));
        oauthAccessToken.put("scope", "uid,name,email,mobile");
        oauthAccessToken.put("token_type", "Bearer");

        return new ResponseEntity<>(oauthAccessToken.toString(), headers, HttpStatus.OK);

    }

    /**
     * 获取OAuth用户信息资源
     *
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<String> userInfo(HttpServletRequest request) throws OAuthSystemException, OAuthProblemException {
        ObjectNode res = new ObjectMapper().createObjectNode();
        res.put("code", 10000);
        res.put("msg", "成功");
        res.set("data", new ObjectMapper().createObjectNode().put("uid", "test"));
        return new ResponseEntity<>(res.toString(), headers, HttpStatus.OK);
    }

}
