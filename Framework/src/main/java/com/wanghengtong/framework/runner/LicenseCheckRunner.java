package com.wanghengtong.framework.runner;

import com.wanghengtong.framework.license.LicenseVerify;
import com.wanghengtong.framework.license.LicenseVerifyEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月27日 22:59
 */
@Slf4j
// @Component
public class LicenseCheckRunner implements ApplicationRunner {
    /**
     * 证书subject
     */
    @Value("${license.subject}")
    private String subject;

    /**
     * 公钥别称
     */
    @Value("${license.publicAlias}")
    private String publicAlias;

    /**
     * 访问公钥库的密码
     */
    @Value("${license.storePass}")
    private String storePass;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("++++++++ 开始安装证书 ++++++++");
        LicenseVerifyEntity param = new LicenseVerifyEntity();
        param.setSubject(subject);
        param.setPublicAlias(publicAlias);
        param.setStorePass(storePass);

        // 使用相对路径获取 keystore 文件
        URL keystoreUrl = getClass().getClassLoader().getResource("publicCerts.keystore");
        if (keystoreUrl == null) {
            throw new Exception("Keystore file not found in resources.");
        }
        File keystoreFile = new File(keystoreUrl.toURI());
        String keystorePath = keystoreFile.getAbsolutePath();
        log.info("Keystore Path: {}", keystorePath);

        // 获取资源路径
        URL resourceUrl = getClass().getClassLoader().getResource("license.lic");
        if (resourceUrl == null) {
            throw new Exception("License file not found in resources.");
        }
        String licensePath = resourceUrl.getPath();
        log.info("License Path: {}", licensePath);

        // 设置参数
        param.setLicensePath(licensePath);
        param.setPublicKeysStorePath(keystorePath);

        // 安装证书
        LicenseVerify licenseVerify = new LicenseVerify();
        try {
            licenseVerify.install(param);
            log.info("++++++++ 证书安装成功 ++++++++");
        } catch (Exception e) {
            log.error("证书安装失败: {}", e.getMessage(), e);
            throw e;
        }
    }

}