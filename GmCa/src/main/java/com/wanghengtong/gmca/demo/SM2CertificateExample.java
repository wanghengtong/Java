package com.wanghengtong.gmca.demo;

import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.FileWriter;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * @author wanghengtong
 * @desc SM2证书生成示例
 * @date 2025年10月17日 15:59
 */
public class SM2CertificateExample {

    public static void main(String[] args) {
        try {
            // 示例1：生成完整的证书链
            generateCertificateChain();

            // 示例2：使用CSR方式生成证书
            // generateWithCSR();

            // 示例3：生成并验证证书
            generateAndVerifyCertificates();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 示例1：生成完整的证书链（根证书 -> 中间证书 -> 用户证书）
     */
    public static void generateCertificateChain() throws Exception {
        System.out.println("=== 生成SM2证书链 ===");

        // 1. 生成根CA密钥对和证书
        KeyPair rootKeyPair = SM2CertificateGenerator.generateSM2KeyPair();
        X509Certificate rootCert = SM2CertificateGenerator.generateRootCertificate(rootKeyPair, "CN=SM2 Root CA, O=My Company, C=CN", 10);
        printCertificateInfo(rootCert, "根证书生成成功");

        // 2. 生成用户密钥对和证书
        KeyPair userKeyPair = SM2CertificateGenerator.generateSM2KeyPair();
        X509Certificate userCert = SM2CertificateGenerator.generateUserCertificateDirect(userKeyPair, rootKeyPair, rootCert, "CN=user@example.com, O=My Company, C=CN", 2);
        printCertificateInfo(userCert, "用户证书生成成功");

        // 3. 保存证书和密钥
        // 保存DER格式证书
        SM2CertificateGenerator.saveCertificateToFile(rootCert, "GMCA/src/main/resources/file/sm2_root_cert.cer");
        SM2CertificateGenerator.saveCertificateToFile(userCert, "GMCA/src/main/resources/file/sm2_user_cert.cer");
        // 保存PEM格式证书
        saveCertificateToPEMFile(rootCert, "GMCA/src/main/resources/file/sm2_root_cert.pem");
        saveCertificateToPEMFile(userCert, "GMCA/src/main/resources/file/sm2_user_cert.pem");
        // 保存密钥
        SM2CertificateGenerator.savePrivateKeyToFile(rootKeyPair.getPrivate(), "GMCA/src/main/resources/file/sm2_root_private.key");
        SM2CertificateGenerator.savePublicKeyToFile(rootKeyPair.getPublic(), "GMCA/src/main/resources/file/sm2_root_public.key");
        SM2CertificateGenerator.savePrivateKeyToFile(userKeyPair.getPrivate(), "GMCA/src/main/resources/file/sm2_user_private.key");
        SM2CertificateGenerator.savePublicKeyToFile(userKeyPair.getPublic(), "GMCA/src/main/resources/file/sm2_user_public.key");

        System.out.println("证书文件已保存到/resources/file目录");
    }

    /**
     * 保存证书为PEM格式文件
     */
    public static void saveCertificateToPEMFile(X509Certificate certificate, String filePath) throws Exception {
        String pemContent = SM2CertificateGenerator.certificateToPEM(certificate);
        java.io.File file = new java.io.File(filePath);
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(pemContent);
        }
    }

    /**
     * 示例2：使用CSR方式生成证书
     */
    public static void generateWithCSR() throws Exception {
        System.out.println("\n=== 使用CSR方式生成证书 ===");

        // 生成根CA
        KeyPair rootKeyPair = SM2CertificateGenerator.generateSM2KeyPair();
        X509Certificate rootCert = SM2CertificateGenerator.generateRootCertificate(rootKeyPair, "CN=SM2 Root CA, O=Test Company, C=CN", 10);

        // 生成用户密钥对和CSR
        KeyPair userKeyPair = SM2CertificateGenerator.generateSM2KeyPair();
        PKCS10CertificationRequest csr = SM2CertificateGenerator.generateCSR(userKeyPair, "CN=server.example.com, O=Test Company, C=CN");

        // 使用CSR签发证书
        X509Certificate userCert = SM2CertificateGenerator.generateUserCertificate(csr, rootKeyPair, rootCert, 1);

        System.out.println("通过CSR生成的证书:");
        printCertificateInfo(userCert, "通过CSR生成的证书成功");

        // 保存证书和密钥
        // 保存DER格式证书
        SM2CertificateGenerator.saveCertificateToFile(rootCert, "GMCA/src/main/resources/file/sm2_root_cert.cer");
        SM2CertificateGenerator.saveCertificateToFile(userCert, "GMCA/src/main/resources/file/sm2_user_cert.cer");
        // 保存PEM格式证书
        saveCertificateToPEMFile(rootCert, "GMCA/src/main/resources/file/sm2_root_cert.pem");
        saveCertificateToPEMFile(userCert, "GMCA/src/main/resources/file/sm2_user_cert.pem");
        // 保存用户密钥
        SM2CertificateGenerator.savePrivateKeyToFile(userKeyPair.getPrivate(), "GMCA/src/main/resources/file/sm2_user_private.key");
        SM2CertificateGenerator.savePublicKeyToFile(userKeyPair.getPublic(), "GMCA/src/main/resources/file/sm2_user_public.key");

        System.out.println("证书文件已保存到/resources/file目录");
    }

    /**
     * 示例3：生成并验证证书
     */
    public static void generateAndVerifyCertificates() throws Exception {
        System.out.println("\n=== 证书验证测试 ===");

        // 生成证书链
        KeyPair rootKeyPair = SM2CertificateGenerator.generateSM2KeyPair();
        X509Certificate rootCert = SM2CertificateGenerator.generateRootCertificate(rootKeyPair, "CN=Test Root CA, O=Test Org, C=CN", 5);

        KeyPair userKeyPair = SM2CertificateGenerator.generateSM2KeyPair();
        X509Certificate userCert = SM2CertificateGenerator.generateUserCertificateDirect(userKeyPair, rootKeyPair, rootCert, "CN=testUser, O=Test Org, C=CN", 1);

        // 验证证书签名
        try {
            userCert.verify(rootKeyPair.getPublic());
            System.out.println("✓ 证书签名验证成功");
        } catch (Exception e) {
            System.out.println("✗ 证书签名验证失败: " + e.getMessage());
            e.printStackTrace();
        }

        // 检查证书有效期
        Date now = new Date();
        if (now.after(userCert.getNotBefore()) && now.before(userCert.getNotAfter())) {
            System.out.println("✓ 证书在有效期内");
        } else {
            System.out.println("✗ 证书已过期或未生效");
        }

        // 验证证书链
        try {
            userCert.verify(rootCert.getPublicKey());
            System.out.println("✓ 证书链验证成功");
        } catch (Exception e) {
            System.out.println("✗ 证书链验证失败: " + e.getMessage());
        }

        // 输出证书签名算法信息
        System.out.println("根证书签名算法: " + rootCert.getSigAlgName());
        System.out.println("用户证书签名算法: " + userCert.getSigAlgName());
        System.out.println("根证书签名算法OID: " + rootCert.getSigAlgOID());
        System.out.println("用户证书签名算法OID: " + userCert.getSigAlgOID());

        // 输出PEM格式
        System.out.println("\n根证书PEM格式:");
        System.out.println(SM2CertificateGenerator.certificateToPEM(rootCert));
    }


    /**
     * 打印证书详细信息
     */
    public static void printCertificateInfo(X509Certificate cert, String title) {
        System.out.println("\n=== " + title + " ===");
        System.out.println("版本: V" + cert.getVersion());
        System.out.println("序列号: " + cert.getSerialNumber().toString(16));
        System.out.println("签名算法: " + cert.getSigAlgName());
        System.out.println("签名算法OID: " + cert.getSigAlgOID());
        System.out.println("颁发者: " + cert.getIssuerDN());
        System.out.println("有效期从: " + cert.getNotBefore());
        System.out.println("到: " + cert.getNotAfter());
        System.out.println("使用者: " + cert.getSubjectDN());
        System.out.println("公钥算法: " + cert.getPublicKey().getAlgorithm());
        System.out.println("公钥格式: " + cert.getPublicKey().getFormat());
        System.out.println();
    }

}