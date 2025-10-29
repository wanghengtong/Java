package com.wanghengtong.gmca.demo;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.Calendar;
import java.util.Date;

/**
 * SM2 证书生成工具类
 */
public class SM2CertificateGenerator {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    // SM2 曲线参数
    private static final String SM2_CURVE_NAME = "sm2p256v1";
    private static final String SIGNATURE_ALGORITHM = "SM3WITHSM2";

    // 国密相关OID
    private static final String SM2_OID = "1.2.156.10197.1.301";
    private static final String SM3_OID = "1.2.156.10197.1.401";
    private static final String SM3WITH_SM2_OID = "1.2.156.10197.1.501";

    /**
     * 生成 SM2 密钥对
     */
    public static KeyPair generateSM2KeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
        ECGenParameterSpec sm2Spec = new ECGenParameterSpec(SM2_CURVE_NAME);
        keyPairGenerator.initialize(sm2Spec, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 生成根证书
     */
    public static X509Certificate generateRootCertificate(KeyPair rootKeyPair,
                                                          String subjectDN,
                                                          int validityYears) throws Exception {
        // 设置证书有效期
        Calendar calendar = Calendar.getInstance();
        Date notBefore = calendar.getTime();
        calendar.add(Calendar.YEAR, validityYears);
        Date notAfter = calendar.getTime();

        // 构建证书信息
        X500Name subject = new X500Name(subjectDN);
        BigInteger serialNumber = generateSecureSerialNumber();

        // 创建证书构建器
        X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(
                subject, // 颁发者（自签名）
                serialNumber,
                notBefore,
                notAfter,
                subject, // 主题
                SubjectPublicKeyInfo.getInstance(rootKeyPair.getPublic().getEncoded())
        );

        JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();

        // 1. 基本约束（必须设置CA:TRUE）
        certificateBuilder.addExtension(
                Extension.basicConstraints,
                true,
                new BasicConstraints(true) // CA=true, pathLenConstraint可选的路径长度
        );

        // 2. 密钥用法（必须包含keyCertSign）
        certificateBuilder.addExtension(
                Extension.keyUsage,
                true,
                new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign | KeyUsage.digitalSignature)
        );

        // 3. 主题密钥标识符
        certificateBuilder.addExtension(
                Extension.subjectKeyIdentifier,
                false,
                extensionUtils.createSubjectKeyIdentifier(rootKeyPair.getPublic())
        );

        // 4. 颁发者密钥标识符（自签名证书与主题相同）
        certificateBuilder.addExtension(
                Extension.authorityKeyIdentifier,
                false,
                extensionUtils.createAuthorityKeyIdentifier(rootKeyPair.getPublic())
        );

        // 5. CRL分发点（可选但推荐）
        certificateBuilder.addExtension(
                Extension.cRLDistributionPoints,
                false,
                createCRLDistributionPoints()
        );

        // 6. 签名算法标识
        certificateBuilder.addExtension(
                Extension.certificatePolicies,
                false,
                createCertificatePolicies()
        );

        // 7. 扩展密钥用法（添加签名算法标识）
        certificateBuilder.addExtension(
                Extension.extendedKeyUsage,
                false,
                createExtendedKeyUsage()
        );

        // 签名证书
        ContentSigner contentSigner = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM)
                .build(rootKeyPair.getPrivate());

        X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certificateHolder);
    }

    /**
     * 生成安全的序列号
     */
    private static BigInteger generateSecureSerialNumber() {
        // 使用当前时间戳 + 随机数
        byte[] randomBytes = new byte[16];
        new SecureRandom().nextBytes(randomBytes);
        return new BigInteger(1, randomBytes);
    }

    /**
     * 创建CRL分发点
     */
    private static CRLDistPoint createCRLDistributionPoints() throws Exception {
        // 这里可以根据实际情况配置CRL分发点URL
        GeneralName gn = new GeneralName(GeneralName.uniformResourceIdentifier, "http://crl.example.com/root.crl");
        GeneralNames gns = new GeneralNames(gn);
        DistributionPointName dpn = new DistributionPointName(0, gns);
        DistributionPoint dp = new DistributionPoint(dpn, null, null);
        return new CRLDistPoint(new DistributionPoint[]{dp});
    }

    /**
     * 创建证书策略（包含签名算法信息）
     */
    private static CertificatePolicies createCertificatePolicies() {
        // 创建一个包含签名算法信息的证书策略
        PolicyInformation policyInfo = new PolicyInformation(
                new ASN1ObjectIdentifier(SM3WITH_SM2_OID),
                new DERSequence(new PolicyQualifierInfo("SM3withSM2 Signature Algorithm"))
        );

        return new CertificatePolicies(new PolicyInformation[]{policyInfo});
    }

    /**
     * 创建扩展密钥用法（包含签名算法信息）
     */
    private static ExtendedKeyUsage createExtendedKeyUsage() {
        // 添加数字签名和证书签名用途
        KeyPurposeId[] usages = new KeyPurposeId[]{
                KeyPurposeId.id_kp_emailProtection,
                KeyPurposeId.id_kp_codeSigning,
                KeyPurposeId.id_kp_clientAuth,
                KeyPurposeId.id_kp_serverAuth
        };

        return new ExtendedKeyUsage(usages);
    }

    /**
     * 生成证书签名请求 (CSR)
     */
    public static PKCS10CertificationRequest generateCSR(KeyPair userKeyPair, String subjectDN) throws Exception {
        X500Name subject = new X500Name(subjectDN);

        PKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(subject, userKeyPair.getPublic());

        ContentSigner contentSigner = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).build(userKeyPair.getPrivate());

        return csrBuilder.build(contentSigner);
    }

    /**
     * 使用根证书签发用户证书
     */
    public static X509Certificate generateUserCertificate(PKCS10CertificationRequest csr, KeyPair rootKeyPair, X509Certificate rootCertificate, int validityYears) throws Exception {
        // 设置证书有效期
        Calendar calendar = Calendar.getInstance();
        Date notBefore = calendar.getTime();
        calendar.add(Calendar.YEAR, validityYears);
        Date notAfter = calendar.getTime();

        // 获取CSR中的公钥和主题
        SubjectPublicKeyInfo publicKeyInfo = csr.getSubjectPublicKeyInfo();
        X500Name subject = csr.getSubject();

        // 创建证书构建器
        X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(new X500Name(rootCertificate.getSubjectX500Principal().getName()), generateSecureSerialNumber(), notBefore, notAfter, subject, publicKeyInfo);

        // 添加基本约束（终端实体证书）
        certificateBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false) // CA=false
        );

        // 添加密钥用法
        certificateBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));

        // 添加增强密钥用法
        certificateBuilder.addExtension(Extension.extendedKeyUsage, true, new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));

        // 添加主题密钥标识符
        JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();
        certificateBuilder.addExtension(Extension.subjectKeyIdentifier, false, extensionUtils.createSubjectKeyIdentifier(publicKeyInfo));

        // 添加颁发者密钥标识符
        certificateBuilder.addExtension(Extension.authorityKeyIdentifier, false, extensionUtils.createAuthorityKeyIdentifier(rootKeyPair.getPublic()));

        // 添加签名算法信息
        certificateBuilder.addExtension(
                Extension.certificatePolicies,
                false,
                createCertificatePolicies()
        );

        // 添加扩展密钥用法（包含签名算法信息）
        certificateBuilder.addExtension(
                Extension.extendedKeyUsage,
                false,
                createExtendedKeyUsage()
        );

        // 签名证书
        ContentSigner contentSigner = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).build(rootKeyPair.getPrivate());

        X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certificateHolder);
    }

    /**
     * 直接生成用户证书（不通过CSR）
     */
    public static X509Certificate generateUserCertificateDirect(KeyPair userKeyPair, KeyPair rootKeyPair, X509Certificate rootCertificate, String subjectDN, int validityYears) throws Exception {
        // 设置证书有效期
        Calendar calendar = Calendar.getInstance();
        Date notBefore = calendar.getTime();
        calendar.add(Calendar.YEAR, validityYears);
        Date notAfter = calendar.getTime();

        // 构建证书信息
        X500Name subject = new X500Name(subjectDN);
        X500Name issuer = new X500Name(rootCertificate.getSubjectX500Principal().getName());

        // 创建证书构建器
        X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(issuer, generateSecureSerialNumber(), notBefore, notAfter, subject, SubjectPublicKeyInfo.getInstance(userKeyPair.getPublic().getEncoded()));

        // 添加扩展
        JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();

        certificateBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));

        certificateBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));

        certificateBuilder.addExtension(Extension.subjectKeyIdentifier, false, extensionUtils.createSubjectKeyIdentifier(userKeyPair.getPublic()));

        certificateBuilder.addExtension(Extension.authorityKeyIdentifier, false, extensionUtils.createAuthorityKeyIdentifier(rootKeyPair.getPublic()));

        // 添加签名算法信息
        certificateBuilder.addExtension(
                Extension.certificatePolicies,
                false,
                createCertificatePolicies()
        );

        // 添加扩展密钥用法（包含签名算法信息）
        certificateBuilder.addExtension(
                Extension.extendedKeyUsage,
                false,
                createExtendedKeyUsage()
        );

        // 签名证书
        ContentSigner contentSigner = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).build(rootKeyPair.getPrivate());

        X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certificateHolder);
    }

    /**
     * 保存证书到文件
     */
    public static void saveCertificateToFile(X509Certificate certificate, String filePath) throws Exception {
        File file = new File(filePath);
        // 确保目录存在
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(certificate.getEncoded());
        }
    }

    /**
     * 保存私钥到文件（PEM格式）
     */
    public static void savePrivateKeyToFile(PrivateKey privateKey, String filePath) throws Exception {
        String encoded = "-----BEGIN PRIVATE KEY-----\n" + Base64.toBase64String(privateKey.getEncoded()) + "\n" + "-----END PRIVATE KEY-----";

        File file = new File(filePath);
        // 确保目录存在
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(encoded);
        }
    }

    /**
     * 保存公钥到文件（PEM格式）
     */
    public static void savePublicKeyToFile(PublicKey publicKey, String filePath) throws Exception {
        String encoded = "-----BEGIN PUBLIC KEY-----\n" + Base64.toBase64String(publicKey.getEncoded()) + "\n" + "-----END PUBLIC KEY-----";

        File file = new File(filePath);
        // 确保目录存在
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(encoded);
        }
    }

    /**
     * 将证书转换为PEM格式字符串
     */
    public static String certificateToPEM(X509Certificate certificate) throws Exception {
        return "-----BEGIN CERTIFICATE-----\n" + Base64.toBase64String(certificate.getEncoded()) + "\n" + "-----END CERTIFICATE-----";
    }
}