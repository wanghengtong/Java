package com.wanghengtong.framework.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月30日 19:44
 */
@TableName("sys_user")
@Data
public class SysUserDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phone;

    private String address;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
