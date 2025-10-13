package com.wanghengtong.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 签名配置类
 *
 * @author wanghengtong
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.signature")
public class SignatureConfig {

    /**
     * 是否启用签名验证
     */
    private boolean enabled = true;

    /**
     * 密钥
     */
    private String secretKey = "your_secret_key_here";

    /**
     * 签名参数名称
     */
    private String signParam = "sign";

    /**
     * 时间戳参数名称
     */
    private String timestampParam = "timestamp";

    /**
     * 随机字符串参数名称
     */
    private String nonceParam = "nonce";

    /**
     * 时间戳有效时间（毫秒） 5分钟
     */
    private long timestampValidity = 5 * 60 * 1000L;

    /**
     * 需要排除在签名外的参数
     */
    private String[] excludeParams = new String[]{"sign"};

    /**
     * 签名算法
     */
    private String algorithm = "md5";

    /**
     * 需要签名验证的路径模式
     */
    private String[] includePatterns = new String[]{"/api/**"};

    /**
     * 不需要签名验证的路径模式
     */
    private String[] excludePatterns = new String[]{};

}