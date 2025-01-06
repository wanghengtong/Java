package com.wanghengtong.framework.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月31日 22:41
 */
@Data
public class SysUserRequest {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phone;

    private String address;

}
