package com.wanghengtong.framework.license;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月27日 22:29
 */
@Data
public class LicenseCreatorEntity {

    /**
     * 证书subject
     */
    private String subject;

    /**
     * 密钥别称
     */
    private String privateAlias;

    /**
     * 公钥密码（需要妥善保管，不能让使用者知道）
     */
    private String keyPass;

    /**
     * 私钥库的密码
     */
    private String storePass;

    /**
     * 证书生成路径
     */
    private String licensePath;

    /**
     * 私钥存储路径
     */
    private String privateKeysStorePath;

    /**
     * 证书生效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date issuedTime;

    /**
     * 证书失效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expiryTime;

    /**
     * 用户类型
     */
    private String consumerType;

    /**
     * 用户数量
     */
    private Integer consumerAmount;

    /**
     * 描述信息
     */
    private String description;

}
