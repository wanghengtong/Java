package com.wanghengtong.gmca.dualdemo;

import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.cert.X509Certificate;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

public class DualCertificateUtils {

    /**
     * 保存双证书和密钥
     */
    public static void saveDualCertificates(
            SM2DualCertificateGenerator.CertificatePair certPair,
            String baseFileName) throws IOException {

        // 保存签名证书和密钥
        saveCertificateToPEM(certPair.getSignatureCert(), "GMCA/src/main/resources/dual/" + baseFileName + "-sign-cert.pem");
        savePrivateKeyToPEM(certPair.getSignatureKeyPair(), "GMCA/src/main/resources/dual/" + baseFileName + "-sign-key.pem");
        // savePublicKeyToPEM(certPair.getSignatureKeyPair(), "GMCA/src/main/resources/dual/" + baseFileName + "-sign-pub.pem");

        // 保存加密证书和密钥
        saveCertificateToPEM(certPair.getEncryptionCert(), "GMCA/src/main/resources/dual/" + baseFileName + "-encrypt-cert.pem");
        savePrivateKeyToPEM(certPair.getEncryptionKeyPair(), "GMCA/src/main/resources/dual/" + baseFileName + "-encrypt-key.pem");
        // savePublicKeyToPEM(certPair.getEncryptionKeyPair(), "GMCA/src/main/resources/dual/" + baseFileName + "-encrypt-pub.pem");
    }

    /**
     * 保存证书到PEM文件
     */
    public static void saveCertificateToPEM(X509Certificate certificate, String filePath) throws IOException {
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter(filePath))) {
            pemWriter.writeObject(certificate);
        }
    }

    /**
     * 保存私钥到PEM文件
     */
    public static void savePrivateKeyToPEM(KeyPair keyPair, String filePath) throws IOException {
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter(filePath))) {
            pemWriter.writeObject(keyPair.getPrivate());
        }
    }

    /**
     * 保存公钥到PEM文件
     */
    public static void savePublicKeyToPEM(KeyPair keyPair, String filePath) throws IOException {
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter(filePath))) {
            pemWriter.writeObject(keyPair.getPublic());
        }
    }
}