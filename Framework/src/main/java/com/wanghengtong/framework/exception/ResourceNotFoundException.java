package com.wanghengtong.framework.exception;

/**
 * @author wanghengtong
 * @desc    ResourceNotFoundException
 * @date 2024年12月25日 21:46
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    } 
}
