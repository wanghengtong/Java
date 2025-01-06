package com.wanghengtong.framework.exception;

/**
 * @author wanghengtong
 * @desc    业务异常
 * @date 2024年12月25日 21:58
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

}
