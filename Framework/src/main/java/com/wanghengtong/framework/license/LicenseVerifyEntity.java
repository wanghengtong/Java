package com.wanghengtong.framework.license;

import lombok.Data;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月27日 18:53
 */
@Data
public class LicenseVerifyEntity {

    /**
     * 证书subject
     */
    private String subject;

    /**
     * 公钥别称
     */
    private String publicAlias;

    /**
     * 访问公钥库的密码
     */
    private String storePass;

    /**
     * 证书生成路径
     */
    private String licensePath;

    /**
     * 密钥库存储路径
     */
    private String publicKeysStorePath;

}