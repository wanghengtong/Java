package com.wanghengtong.framework.exception;

import com.wanghengtong.framework.common.ApiResponse;
import com.wanghengtong.framework.enums.HttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * @author wanghengtong
 * @desc 全局异常处理
 * @date 2024年12月25日 21:45
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ApiResponse.error(HttpCodeEnum.ERROR.getCode(), e.getMessage());
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ApiResponse<?> handleValidationException(Exception e) {
        log.warn("参数验证失败: {}", e.getMessage());
        BindingResult bindingResult;
        if (e instanceof BindException) {
            bindingResult = ((BindException) e).getBindingResult();
        } else {
            bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
        }

        StringBuilder errorMessage = new StringBuilder();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            errorMessage.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("; ");
        }

        // 移除末尾的分号和空格
        if (errorMessage.length() > 0) {
            errorMessage.setLength(errorMessage.length() - 2);
        }

        return ApiResponse.error(HttpCodeEnum.RC400.getCode(), errorMessage.toString());
    }

    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return ApiResponse.error(HttpCodeEnum.ERROR.getCode(), "系统内部错误");
    }

}