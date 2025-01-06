package com.wanghengtong.framework.license;

import de.schlichtherle.license.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.prefs.Preferences;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月27日 18:52
 */
@Slf4j
public class LicenseVerify {

    /**
     * 安装License证书
     *
     * @param param
     * @return
     */
    public synchronized LicenseContent install(LicenseVerifyEntity param) {
        LicenseContent result = null;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 1. 安装证书
        try {
            LicenseManager licenseManager = LicenseManagerHolder.getInstance(initLicenseParam(param));
            licenseManager.uninstall();
            result = licenseManager.install(new File(param.getLicensePath()));
            log.info(MessageFormat.format("证书安装成功，证书有效期：{0} - {1}", format.format(result.getNotBefore()), format.format(result.getNotAfter())));
        } catch (Exception e) {
            log.error("证书安装失败: {}", e.getMessage());
        }
        return result;
    }


    /**
     * 校验License证书
     *
     * @return
     */
    public boolean verify() {
        LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 2. 校验证书
        try {
            LicenseContent licenseContent = licenseManager.verify();
            log.info(MessageFormat.format("证书校验通过，证书有效期：{0} - {1}", format.format(licenseContent.getNotBefore()), format.format(licenseContent.getNotAfter())));
            return true;
        } catch (Exception e) {
            log.error("证书校验失败: {}", e.getMessage());
            return false;
        }
    }

    // 初始化证书生成参数
    private LicenseParam initLicenseParam(LicenseVerifyEntity param) {
        Preferences preferences = Preferences.userNodeForPackage(LicenseVerify.class);
        CipherParam cipherParam = new DefaultCipherParam(param.getStorePass());
        KeyStoreParam publicStoreParam = new CustomKeyStoreEntity(LicenseVerify.class, param.getPublicKeysStorePath(), param.getPublicAlias(), param.getStorePass(), null);
        return new DefaultLicenseParam(param.getSubject(), preferences, publicStoreParam, cipherParam);
    }

}
