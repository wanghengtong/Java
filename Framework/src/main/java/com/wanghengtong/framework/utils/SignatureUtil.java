package com.wanghengtong.framework.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * 签名工具类
 *
 * @author wanghengtong
 */
@Slf4j
public class SignatureUtil {

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    /**
     * 生成签名（统一方法）
     *
     * @param params      URL参数Map
     * @param body        请求体内容（POST请求使用）
     * @param secretKey   密钥
     * @param excludeKeys 不参与签名的字段
     * @return 签名字符串
     */
    public static String generateSignature(Map<String, String[]> params, String body, String secretKey, String... excludeKeys) {
        // 使用TreeMap保证排序
        Map<String, String> sortedParams = new TreeMap<>();

        // 过滤并转换参数
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();

            // 跳过不需要签名的参数
            if (Arrays.asList(excludeKeys).contains(key)) {
                continue;
            }

            // 处理多值参数，只取第一个值
            if (values != null && values.length > 0 && StringUtils.hasText(values[0])) {
                sortedParams.put(key, values[0]);
            }
        }

        // 构建待签名字符串
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        // 如果有请求体，则添加到签名字符串中
        if (StringUtils.hasText(body)) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append("body=").append(body);
        }

        // 添加密钥
        sb.append("&key=").append(secretKey);

        // 生成签名
        String signStr = sb.toString();
        log.debug("Sign String: {}", signStr);
        return DigestUtils.md5DigestAsHex(signStr.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 验证签名
     *
     * @param params      URL参数Map
     * @param body        请求体内容（POST请求使用）
     * @param secretKey   密钥
     * @param sign        待验证的签名
     * @param excludeKeys 不参与签名的字段
     * @return 是否验证通过
     */
    public static boolean verifySignature(Map<String, String[]> params, String body, String secretKey, String sign, String... excludeKeys) {
        if (!StringUtils.hasText(sign)) {
            return false;
        }

        String generatedSign = generateSignature(params, body, secretKey, excludeKeys);
        return generatedSign.equalsIgnoreCase(sign);
    }

    /**
     * 使用HMAC-SHA256算法生成签名（统一方法）
     *
     * @param params      URL参数Map
     * @param body        请求体内容（POST请求使用）
     * @param secretKey   密钥
     * @param excludeKeys 不参与签名的字段
     * @return 签名字符串
     */
    public static String generateHmacSha256Signature(Map<String, String[]> params, String body, String secretKey, String... excludeKeys) {
        // 使用TreeMap保证排序
        Map<String, String> sortedParams = new TreeMap<>();

        // 过滤并转换参数
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();

            // 跳过不需要签名的参数
            if (Arrays.asList(excludeKeys).contains(key)) {
                continue;
            }

            // 处理多值参数，只取第一个值
            if (values != null && values.length > 0 && StringUtils.hasText(values[0])) {
                sortedParams.put(key, values[0]);
            }
        }

        // 构建待签名字符串
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        // 如果有请求体，则添加到签名字符串中
        if (StringUtils.hasText(body)) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append("body=").append(body);
        }

        // 添加密钥
        sb.append("&key=").append(secretKey);

        // 生成HMAC-SHA256签名
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA256_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(sb.toString().getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash).toUpperCase();
        } catch (Exception e) {
            log.error("Generate HMAC-SHA256 signature error", e);
            throw new RuntimeException("生成签名失败", e);
        }
    }

    /**
     * 验证HMAC-SHA256签名（统一方法）
     *
     * @param params      URL参数Map
     * @param body        请求体内容（POST请求使用）
     * @param secretKey   密钥
     * @param sign        待验证的签名
     * @param excludeKeys 不参与签名的字段
     * @return 是否验证通过
     */
    public static boolean verifyHmacSha256Signature(Map<String, String[]> params, String body, String secretKey, String sign, String... excludeKeys) {
        if (!StringUtils.hasText(sign)) {
            return false;
        }

        String generatedSign = generateHmacSha256Signature(params, body, secretKey, excludeKeys);
        return generatedSign.equalsIgnoreCase(sign);
    }

    /**
     * 从HttpServletRequest中读取请求体
     *
     * @param request HttpServletRequest
     * @return 请求体字符串
     */
    public static String getBodyString(HttpServletRequest request) {
        try {
            return StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("读取请求体失败", e);
            return "";
        }
    }

    /**
     * 字节数组转十六进制字符串
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}