package com.wanghengtong.gmca.dualdemo;

import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;

public class CertificateUtils {

    /**
     * 打印证书详细信息
     */
    public static void printCertificateInfo(X509Certificate cert, String title) {
        System.out.println("\n=== " + title + " ===");
        System.out.println("版本: V" + cert.getVersion());
        System.out.println("序列号: " + cert.getSerialNumber().toString(16));
        System.out.println("签名算法: " + cert.getSigAlgName());
        System.out.println("颁发者: " + cert.getIssuerDN());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("有效期从: " + sdf.format(cert.getNotBefore()));
        System.out.println("到: " + sdf.format(cert.getNotAfter()));

        System.out.println("使用者: " + cert.getSubjectDN());
        System.out.println("公钥算法: " + cert.getPublicKey().getAlgorithm());

        // 打印密钥用途
        if (cert.getKeyUsage() != null) {
            System.out.println("密钥用途: " + getKeyUsageDescription(cert));
        }
    }

    /**
     * 获取密钥用途描述
     */
    public static String getKeyUsageDescription(X509Certificate cert) {
        boolean[] keyUsage = cert.getKeyUsage();
        if (keyUsage == null) return "未设置";

        StringBuilder sb = new StringBuilder();
        if (keyUsage.length > 0 && keyUsage[0]) sb.append("数字签名 ");
        if (keyUsage.length > 1 && keyUsage[1]) sb.append("不可否认 ");
        if (keyUsage.length > 2 && keyUsage[2]) sb.append("密钥加密 ");
        if (keyUsage.length > 3 && keyUsage[3]) sb.append("数据加密 ");
        if (keyUsage.length > 4 && keyUsage[4]) sb.append("密钥协商 ");
        if (keyUsage.length > 5 && keyUsage[5]) sb.append("证书签名 ");
        if (keyUsage.length > 6 && keyUsage[6]) sb.append("CRL签名 ");

        return sb.toString().trim();
    }
}
