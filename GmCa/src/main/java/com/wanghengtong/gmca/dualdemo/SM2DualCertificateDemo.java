package com.wanghengtong.gmca.dualdemo;

import java.security.KeyPair;
import java.security.Signature;
import java.security.cert.X509Certificate;

public class SM2DualCertificateDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("=== 国密SM2双证书生成演示 ===\n");

        // 1. 生成CA根证书
        System.out.println("1. 生成CA根证书...");
        KeyPair caKeyPair = SM2DualCertificateGenerator.generateSM2KeyPair();
        X509Certificate caCert = SM2DualCertificateGenerator.generateRootCertificate(caKeyPair, "CN=SM2 Dual Certificate CA, O=My Company", 3650);

        CertificateUtils.printCertificateInfo(caCert, "CA根证书");

        // 2. 生成双证书（签名证书 + 加密证书）
        System.out.println("\n2. 生成双证书...");
        SM2DualCertificateGenerator.CertificatePair dualCerts = SM2DualCertificateGenerator.generateDualCertificates(caKeyPair, caCert, "CN=Test User, O=My Company, C=CN", 365);

        // 3. 打印签名证书信息
        System.out.println("\n3. 签名证书信息:");
        CertificateUtils.printCertificateInfo(dualCerts.getSignatureCert(), "签名证书");

        // 4. 打印加密证书信息
        System.out.println("\n4. 加密证书信息:");
        CertificateUtils.printCertificateInfo(dualCerts.getEncryptionCert(), "加密证书");

        // 5. 验证证书和私钥是否匹配
        System.out.println("\n5. 验证证书和私钥匹配性...");
        boolean signCertValid = validateCertKeyPair(dualCerts.getSignatureCert(), dualCerts.getSignatureKeyPair());
        boolean encryptCertValid = validateCertKeyPair(dualCerts.getEncryptionCert(), dualCerts.getEncryptionKeyPair());
        
        System.out.println("  签名证书和私钥匹配: " + (signCertValid ? "✓ 是" : "✗ 否"));
        System.out.println("  加密证书和私钥匹配: " + (encryptCertValid ? "✓ 是" : "✗ 否"));

        // 6. 验证证书差异
        System.out.println("\n6. 证书差异分析:");
        analyzeCertificateDifferences(dualCerts);

        // 7. 验证证书签名
        System.out.println("\n7. 验证证书签名...");
        try {
            dualCerts.getSignatureCert().verify(caKeyPair.getPublic());
            dualCerts.getEncryptionCert().verify(caKeyPair.getPublic());
            System.out.println("✓ 双证书签名验证成功！");
        } catch (Exception e) {
            System.out.println("✗ 证书签名验证失败: " + e.getMessage());
        }

        // 8. 保存所有证书和密钥
        System.out.println("\n8. 保存证书文件...");
        DualCertificateUtils.saveDualCertificates(dualCerts, "sm2");
        // DualCertificateUtils.saveCertificateToPEM(caCert, "sm2");
        // DualCertificateUtils.savePrivateKeyToPEM(caKeyPair, "sm2");

        System.out.println("\n✓ 双证书生成完成！");
        System.out.println("生成的文件:");
        System.out.println("sign-cert.pem (签名证书)");
        System.out.println("sign-key.pem (签名私钥)");
        // System.out.println("sign-pub.pem (签名公钥)");
        System.out.println("encrypt-cert.pem (加密证书)");
        System.out.println("encrypt-key.pem (加密私钥)");
        // System.out.println("encrypt-pub.pem (加密公钥)");
        // System.out.println("sm2-ca-cert.pem (CA根证书)");
        // System.out.println("sm2-ca-key.pem (CA私钥)");
    }

    /**
     * 验证证书和私钥是否匹配
     */
    private static boolean validateCertKeyPair(X509Certificate cert, KeyPair keyPair) {
        try {
            // 使用私钥签名一些数据
            Signature signature = Signature.getInstance("SM3withSM2", "BC");
            signature.initSign(keyPair.getPrivate());
            
            byte[] testData = "test data for validation".getBytes();
            signature.update(testData);
            byte[] signResult = signature.sign();
            
            // 使用证书中的公钥验证签名
            signature.initVerify(cert.getPublicKey());
            signature.update(testData);
            
            return signature.verify(signResult);
        } catch (Exception e) {
            System.out.println("验证过程中出现错误: " + e.getMessage());
            return false;
        }
    }

    /**
     * 分析双证书的差异
     */
    private static void analyzeCertificateDifferences(SM2DualCertificateGenerator.CertificatePair dualCerts) {
        X509Certificate signCert = dualCerts.getSignatureCert();
        X509Certificate encryptCert = dualCerts.getEncryptionCert();

        System.out.println("序列号差异:");
        System.out.println("  签名证书: " + signCert.getSerialNumber().toString(16));
        System.out.println("  加密证书: " + encryptCert.getSerialNumber().toString(16));

        System.out.println("主题名称差异:");
        System.out.println("  签名证书: " + signCert.getSubjectDN());
        System.out.println("  加密证书: " + encryptCert.getSubjectDN());

        System.out.println("密钥用途差异:");
        System.out.println("  签名证书: " + CertificateUtils.getKeyUsageDescription(signCert));
        System.out.println("  加密证书: " + CertificateUtils.getKeyUsageDescription(encryptCert));

        System.out.println("密钥对分离:");
        System.out.println("  签名密钥对: 独立生成");
        System.out.println("  加密密钥对: 独立生成");
    }

}