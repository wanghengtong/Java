package com.wanghengtong.oauth2serverdemo.controller;

import com.wanghengtong.oauth2serverdemo.service.IOAuth2IdpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author wanghengtong
 * @desc
 * @date 2025年04月27日 20:43
 */
@Slf4j
@RestController
@RequestMapping("/oauth2.0")
public class OAuth2IdpController {

    @Autowired
    private IOAuth2IdpService oAuth2IdpService;

    /**
     * 获取OAuth授权码
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/code")
    public RedirectView authorize(HttpServletRequest request, HttpServletResponse response) {
        printRequestParameter(request);
        return oAuth2IdpService.authorize(request, response);
    }

    /**
     * 获取OAuth访问令牌
     *
     * @param request
     * @return
     */
    @RequestMapping("/token")
    public ResponseEntity<String> accessToken(HttpServletRequest request) throws Exception {
        printRequestParameter(request);
        return oAuth2IdpService.accessToken(request);
    }

    /**
     * 获取OAuth用户信息资源
     *
     * @param request
     * @return
     */
    @RequestMapping("/res")
    public ResponseEntity<String> userInfo(HttpServletRequest request) throws Exception {
        printRequestParameter(request);
        return oAuth2IdpService.userInfo(request);
    }

    public static void printRequestParameter(HttpServletRequest request) {
        log.info("请求URI: {}", request.getRequestURI());
        Map<String, String[]> map = request.getParameterMap();
        Set<Map.Entry<String, String[]>> keys = map.entrySet();
        Iterator<Map.Entry<String, String[]>> it = keys.iterator();
        while (it.hasNext()) {
            Map.Entry<String, String[]> itMap = it.next();
            log.info("请求入参: {}", itMap.getKey() + ":" + Arrays.toString(itMap.getValue()));
        }
    }

}
