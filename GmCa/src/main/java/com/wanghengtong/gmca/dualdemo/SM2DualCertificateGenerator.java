package com.wanghengtong.gmca.dualdemo;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Vector;

public class SM2DualCertificateGenerator {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String SIGNATURE_ALGORITHM = "SM3withSM2";
    private static final String SM2_CURVE_NAME = "sm2p256v1";

    /**
     * 证书类型枚举
     */
    public enum CertificateType {
        SIGNATURE("签名证书", "1.2.156.10197.6.2.1.1"),
        ENCRYPTION("加密证书", "1.2.156.10197.6.2.1.2");

        private final String description;
        private final String policyOID;

        CertificateType(String description, String policyOID) {
            this.description = description;
            this.policyOID = policyOID;
        }

        public String getDescription() {
            return description;
        }

        public String getPolicyOID() {
            return policyOID;
        }
    }

    /**
     * 证书对结果
     */
    public static class CertificatePair {
        private final X509Certificate signatureCert;
        private final X509Certificate encryptionCert;
        private final KeyPair signatureKeyPair;
        private final KeyPair encryptionKeyPair;

        public CertificatePair(X509Certificate signatureCert, X509Certificate encryptionCert,
                               KeyPair signatureKeyPair, KeyPair encryptionKeyPair) {
            this.signatureCert = signatureCert;
            this.encryptionCert = encryptionCert;
            this.signatureKeyPair = signatureKeyPair;
            this.encryptionKeyPair = encryptionKeyPair;
        }

        // Getters
        public X509Certificate getSignatureCert() {
            return signatureCert;
        }

        public X509Certificate getEncryptionCert() {
            return encryptionCert;
        }

        public KeyPair getSignatureKeyPair() {
            return signatureKeyPair;
        }

        public KeyPair getEncryptionKeyPair() {
            return encryptionKeyPair;
        }
    }

    /**
     * 生成SM2密钥对
     */
    public static KeyPair generateSM2KeyPair() throws Exception {
        // 方法1: 使用标准EC算法指定SM2曲线
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");

        // 使用SM2曲线参数
        ECNamedCurveParameterSpec sm2Spec = ECNamedCurveTable.getParameterSpec(SM2_CURVE_NAME);
        keyPairGenerator.initialize(sm2Spec);

        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 生成双证书（签名证书 + 加密证书）
     */
    public static CertificatePair generateDualCertificates(
            KeyPair caKeyPair,
            X509Certificate caCert,
            String subjectDN,
            int validityDays) throws Exception {

        // 生成签名证书密钥对和证书
        KeyPair signatureKeyPair = generateSM2KeyPair();
        X509Certificate signatureCert = generateEndEntityCertificate(
                signatureKeyPair, caKeyPair, caCert, subjectDN, validityDays, CertificateType.SIGNATURE);

        // 生成加密证书密钥对和证书
        KeyPair encryptionKeyPair = generateSM2KeyPair();
        X509Certificate encryptionCert = generateEndEntityCertificate(
                encryptionKeyPair, caKeyPair, caCert, subjectDN, validityDays, CertificateType.ENCRYPTION);

        return new CertificatePair(signatureCert, encryptionCert, signatureKeyPair, encryptionKeyPair);
    }

    /**
     * 生成终端实体证书（根据类型区分签名/加密）
     */
    private static X509Certificate generateEndEntityCertificate(
            KeyPair subjectKeyPair,
            KeyPair caKeyPair,
            X509Certificate caCert,
            String subjectDN,
            int validityDays,
            CertificateType certType) throws Exception {

        // 为不同证书类型添加标识
        String actualSubjectDN = subjectDN + ", CN=" + getCommonNameForCertType(certType);
        X500Name subject = new X500Name(actualSubjectDN);
        X500Name issuer = new X500Name(caCert.getSubjectX500Principal().getName());

        BigInteger serial = generateSerialNumber(certType);
        Date notBefore = new Date();
        Date notAfter = new Date(notBefore.getTime() + validityDays * 24L * 60 * 60 * 1000);

        // 使用JcaX509v3CertificateBuilder来避免公钥转换问题
        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
                issuer, serial, notBefore, notAfter, subject,
                SubjectPublicKeyInfo.getInstance(
                        subjectKeyPair.getPublic().getEncoded()));

        // 添加标准扩展
        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

        certBuilder.addExtension(
                Extension.subjectKeyIdentifier,
                false,
                extUtils.createSubjectKeyIdentifier(subjectKeyPair.getPublic()));

        certBuilder.addExtension(
                Extension.authorityKeyIdentifier,
                false,
                extUtils.createAuthorityKeyIdentifier(caKeyPair.getPublic()));

        certBuilder.addExtension(
                Extension.basicConstraints,
                true,
                new BasicConstraints(false));

        // 根据证书类型设置不同的密钥用途
        certBuilder.addExtension(
                Extension.keyUsage,
                true,
                createKeyUsageForType(certType));

        // 增强密钥用途
        certBuilder.addExtension(
                Extension.extendedKeyUsage,
                false,
                createExtendedKeyUsageForType(certType));

        // 添加证书策略扩展
        certBuilder.addExtension(
                Extension.certificatePolicies,
                false,
                createCertificatePoliciesForType(certType));

        // 添加自定义扩展来标识证书类型
        certBuilder.addExtension(
                new ASN1ObjectIdentifier("1.2.156.10197.6.100"), // 自定义OID
                false,
                new DERUTF8String(certType.getDescription()));

        ContentSigner signer = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM)
                .setProvider("BC")
                .build(caKeyPair.getPrivate());

        X509CertificateHolder certHolder = certBuilder.build(signer);

        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certHolder);
    }

    /**
     * 根据证书类型生成不同的序列号
     */
    private static BigInteger generateSerialNumber(CertificateType certType) {
        long base = System.currentTimeMillis();
        // 签名证书使用偶数序列号，加密证书使用奇数序列号
        long serial = certType == CertificateType.SIGNATURE ? base & ~1L : base | 1L;
        return BigInteger.valueOf(serial);
    }

    /**
     * 根据证书类型获取不同的CN
     */
    private static String getCommonNameForCertType(CertificateType certType) {
        switch (certType) {
            case SIGNATURE:
                return "Signature Certificate";
            case ENCRYPTION:
                return "Encryption Certificate";
            default:
                return "Certificate";
        }
    }

    /**
     * 根据证书类型创建密钥用途
     */
    private static KeyUsage createKeyUsageForType(CertificateType certType) {
        switch (certType) {
            case SIGNATURE:
                // 签名证书：数字签名、不可否认
                return new KeyUsage(KeyUsage.digitalSignature | KeyUsage.nonRepudiation);
            case ENCRYPTION:
                // 加密证书：密钥加密、数据加密、密钥协商
                return new KeyUsage(KeyUsage.keyEncipherment | KeyUsage.dataEncipherment | KeyUsage.keyAgreement);
            default:
                return new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment);
        }
    }

    /**
     * 根据证书类型创建增强密钥用途
     */
    private static ExtendedKeyUsage createExtendedKeyUsageForType(CertificateType certType) {
        Vector<KeyPurposeId> keyPurposes = new Vector<>();

        switch (certType) {
            case SIGNATURE:
                // 签名证书：客户端认证、服务器认证、代码签名
                keyPurposes.add(KeyPurposeId.id_kp_clientAuth);
                keyPurposes.add(KeyPurposeId.id_kp_serverAuth);
                keyPurposes.add(KeyPurposeId.id_kp_codeSigning);
                break;
            case ENCRYPTION:
                // 加密证书：电子邮件保护
                keyPurposes.add(KeyPurposeId.id_kp_emailProtection);
                keyPurposes.add(KeyPurposeId.id_kp_ipsecEndSystem);
                keyPurposes.add(KeyPurposeId.id_kp_ipsecTunnel);
                break;
        }

        return new ExtendedKeyUsage(keyPurposes.toArray(new KeyPurposeId[0]));
    }

    /**
     * 根据证书类型创建证书策略
     */
    private static CertificatePolicies createCertificatePoliciesForType(CertificateType certType) {
        Vector<PolicyInformation> policies = new Vector<>();

        // 使用证书类型特定的策略OID
        ASN1ObjectIdentifier policyOID = new ASN1ObjectIdentifier(certType.getPolicyOID());
        PolicyInformation policyInfo = new PolicyInformation(policyOID);
        policies.add(policyInfo);

        return new CertificatePolicies(policies.toArray(new PolicyInformation[0]));
    }

    /**
     * 生成根证书（用于签发双证书）
     */
    public static X509Certificate generateRootCertificate(KeyPair keyPair, String subjectDN, int validityDays)
            throws Exception {

        X500Name subject = new X500Name(subjectDN);
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = new Date();
        Date notAfter = new Date(notBefore.getTime() + validityDays * 24L * 60 * 60 * 1000);

        // 使用SubjectPublicKeyInfo来避免公钥转换问题
        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
                subject, serial, notBefore, notAfter, subject,
                SubjectPublicKeyInfo.getInstance(
                        keyPair.getPublic().getEncoded()));

        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

        certBuilder.addExtension(
                Extension.subjectKeyIdentifier,
                false,
                extUtils.createSubjectKeyIdentifier(keyPair.getPublic()));

        certBuilder.addExtension(
                Extension.authorityKeyIdentifier,
                false,
                extUtils.createAuthorityKeyIdentifier(keyPair.getPublic()));

        certBuilder.addExtension(
                Extension.basicConstraints,
                true,
                new BasicConstraints(true));

        KeyUsage keyUsage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign);
        certBuilder.addExtension(
                Extension.keyUsage,
                true,
                keyUsage);

        ContentSigner signer = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM)
                .setProvider("BC")
                .build(keyPair.getPrivate());

        X509CertificateHolder certHolder = certBuilder.build(signer);

        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certHolder);
    }
}
