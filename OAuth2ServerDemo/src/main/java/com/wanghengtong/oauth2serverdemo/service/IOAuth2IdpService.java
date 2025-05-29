package com.wanghengtong.oauth2serverdemo.service;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wanghengtong
 * @desc
 * @date 2025年04月27日 20:47
 */
public interface IOAuth2IdpService {

    /**
     * 获取OAuth授权码
     *
     * @param request
     * @param response
     */
    public RedirectView authorize(HttpServletRequest request, HttpServletResponse response);

    /**
     * 获取OAuth授权令牌
     *
     * @param request
     * @return
     */
    public ResponseEntity<String> accessToken(HttpServletRequest request) throws OAuthProblemException, OAuthSystemException;

    /**
     * 获取OAuth用户信息资源
     *
     * @param request
     * @return
     */
    public ResponseEntity<String> userInfo(HttpServletRequest request) throws OAuthSystemException, OAuthProblemException;

}
