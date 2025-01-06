package com.wanghengtong.framework.controller;

import com.wanghengtong.framework.common.ApiResponse;
import com.wanghengtong.framework.enums.HttpCodeEnum;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wanghengtong
 * @desc MyErrorController
 * @date 2024年12月25日 22:28
 */
@RestController
public class MyErrorController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(PATH)
    public ResponseEntity<ApiResponse<String>> handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode != null) {
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(HttpCodeEnum.RC404));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Internal Server Error"));
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Internal Server Error"));
    }

}