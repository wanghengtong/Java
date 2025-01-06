package com.wanghengtong.framework.common;

import com.wanghengtong.framework.enums.HttpCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wanghengtong
 * @desc 统一响应
 * @date 2024年12月25日 21:41
 */
@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private Integer code;

    private String message;

    private T data;

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(HttpCodeEnum.SUCCESS.getCode(), HttpCodeEnum.SUCCESS.getMessage(), null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpCodeEnum.SUCCESS.getCode(), HttpCodeEnum.SUCCESS.getMessage(), data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(HttpCodeEnum.ERROR.getCode(), message, null);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> error(HttpCodeEnum httpCodeEnum) {
        return new ApiResponse<>(httpCodeEnum.getCode(), httpCodeEnum.getMessage(), null);
    }

}
