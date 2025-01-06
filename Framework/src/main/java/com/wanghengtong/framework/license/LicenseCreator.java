package com.wanghengtong.framework.license;

import de.schlichtherle.license.*;
import lombok.extern.slf4j.Slf4j;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.prefs.Preferences;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月27日 22:28
 */
@Slf4j
public class LicenseCreator {

    private final static X500Principal DEFAULT_HOLDER_AND_ISSUER = new X500Principal("CN=wanghengtong, OU=wanghengtong.cn, O=dev, L=BJ, ST=BJ, C=CN");

    public static void main(String[] args) throws Exception {
        LicenseCreatorEntity param = new LicenseCreatorEntity();
        // 证书主题
        param.setSubject("license");
        // 密钥别称
        param.setPrivateAlias("privateKey");
        // 私钥密码
        param.setKeyPass("private_password1234");
        // 私钥库的密码
        param.setStorePass("public_password1234");
        // 证书生成路径
        param.setLicensePath("C:\\Users\\DELL\\Desktop\\lic\\license.lic");
        // 私钥存储路径
        // 使用类路径获取 keystore 文件
        URL keystoreUrl = LicenseCreator.class.getClassLoader().getResource("cer/privateKeys.keystore");
        if (keystoreUrl == null) {
            throw new Exception("Keystore file not found in resources.");
        }
        File keystoreFile = new File(keystoreUrl.toURI());
        String keystorePath = keystoreFile.getAbsolutePath();
        log.info("Keystore Path: {}", keystorePath);

        // 设置参数
        param.setPrivateKeysStorePath(keystorePath);
        // 证书生成时间-当前时间
        param.setIssuedTime(new Date());
        LocalDateTime localDateTime = LocalDateTime.of(2024, 12, 31, 23, 59, 59);
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        // 证书过期时间-2024年12月31日23:59:59
        param.setExpiryTime(date);
        // 用户类型
        param.setConsumerType("user");
        // 用户数量
        param.setConsumerAmount(1);
        // 证书描述
        param.setDescription("证书描述信息");
        // 生成证书
        LicenseCreator licenseCreator = new LicenseCreator();
        licenseCreator.generateLicense(param);
    }

    // 生成License证书
    public void generateLicense(LicenseCreatorEntity param) {
        try {
            LicenseManager licenseManager = new LicenseManager(initLicenseParam(param));
            LicenseContent licenseContent = initLicenseContent(param);
            licenseManager.store(licenseContent, new File(param.getLicensePath()));
        } catch (Exception e) {
            log.error("证书生成失败", e);
        }
    }

    // 初始化证书生成参数
    private static LicenseParam initLicenseParam(LicenseCreatorEntity param) {
        Preferences preferences = Preferences.userNodeForPackage(LicenseCreator.class);
        // 设置对证书内容加密的秘钥
        CipherParam cipherParam = new DefaultCipherParam(param.getStorePass());
        // 自定义KeyStoreParam
        KeyStoreParam privateStoreParam = new CustomKeyStoreEntity(LicenseCreator.class, param.getPrivateKeysStorePath(), param.getPrivateAlias(), param.getStorePass(), param.getKeyPass());
        // 组织License参数
        return new DefaultLicenseParam(param.getSubject(), preferences, privateStoreParam, cipherParam);
    }

    // 设置证书生成正文信息
    private static LicenseContent initLicenseContent(LicenseCreatorEntity param) {
        LicenseContent licenseContent = new LicenseContent();
        licenseContent.setHolder(DEFAULT_HOLDER_AND_ISSUER);
        licenseContent.setIssuer(DEFAULT_HOLDER_AND_ISSUER);
        licenseContent.setSubject(param.getSubject());
        licenseContent.setIssued(param.getIssuedTime());
        licenseContent.setNotBefore(param.getIssuedTime());
        licenseContent.setNotAfter(param.getExpiryTime());
        licenseContent.setConsumerType(param.getConsumerType());
        licenseContent.setConsumerAmount(param.getConsumerAmount());
        licenseContent.setInfo(param.getDescription());
        return licenseContent;
    }

}
