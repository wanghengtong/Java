package com.wanghengtong.gmca.demo;

import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * PKCS12 密钥库工具
 */
public class PKCS12KeyStoreUtil {

    /**
     * 创建包含用户证书和私钥的PKCS12文件
     */
    public static void createPKCS12KeyStore(String filePath,
                                            String password,
                                            String alias,
                                            PrivateKey privateKey,
                                            X509Certificate userCertificate,
                                            X509Certificate... chainCertificates) throws Exception {

        KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
        keyStore.load(null, null);

        // 构建证书链
        Certificate[] certificateChain;
        if (chainCertificates != null && chainCertificates.length > 0) {
            certificateChain = new Certificate[chainCertificates.length + 1];
            certificateChain[0] = userCertificate;
            System.arraycopy(chainCertificates, 0, certificateChain, 1, chainCertificates.length);
        } else {
            certificateChain = new Certificate[]{userCertificate};
        }

        // 设置条目
        keyStore.setKeyEntry(alias, privateKey, password.toCharArray(), certificateChain);

        // 保存密钥库
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            keyStore.store(fos, password.toCharArray());
        }

        System.out.println("PKCS12密钥库已创建: " + filePath);
    }

    /**
     * 示例：创建包含完整证书链的PKCS12文件
     */
    public static void example() throws Exception {
        // 生成根证书
        KeyPair rootKeyPair = SM2CertificateGenerator.generateSM2KeyPair();
        X509Certificate rootCert = SM2CertificateGenerator.generateRootCertificate(
                rootKeyPair, "CN=Root CA", 10);

        // 生成用户证书
        KeyPair userKeyPair = SM2CertificateGenerator.generateSM2KeyPair();
        X509Certificate userCert = SM2CertificateGenerator.generateUserCertificateDirect(
                userKeyPair, rootKeyPair, rootCert, "CN=User", 1);

        // 创建PKCS12文件
        createPKCS12KeyStore(
                "user_certificate.p12",
                "password123",
                "user-alias",
                userKeyPair.getPrivate(),
                userCert,
                rootCert
        );
    }

}