package com.wanghengtong.framework.utils;

import org.springframework.util.DigestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * 签名生成工具类
 */
public class SignatureGeneratorUtil {

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    /**
     * 为GET请求生成签名（MD5算法）
     *
     * @param params    请求参数
     * @param secretKey 密钥
     * @return 签名值
     */
    public static String generateGetSignature(Map<String, String> params, String secretKey) {
        // 使用TreeMap保证排序
        Map<String, String> sortedParams = new TreeMap<>(params);

        // 构建待签名字符串
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        // 添加密钥
        sb.append("&key=").append(secretKey);

        // 生成签名
        String signStr = sb.toString();
        System.out.println("Sign String: " + signStr);
        return DigestUtils.md5DigestAsHex(signStr.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 为GET请求生成完整URL
     *
     * @param baseUrl   基础URL
     * @param params    请求参数
     * @param secretKey 密钥
     * @return 完整的带签名参数的URL
     */
    public static String generateGetSignatureUrl(String baseUrl, Map<String, String> params, String secretKey) {
        // 添加签名所需参数
        long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString().replace("-", "");

        params.put("timestamp", String.valueOf(timestamp));
        params.put("nonce", nonce);

        // 使用TreeMap保证排序
        Map<String, String> sortedParams = new TreeMap<>(params);

        // 生成签名
        String sign = generateGetSignature(sortedParams, secretKey);

        // 构建完整URL（注意参数顺序要和签名时一致）
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl);
        boolean first = true;
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (first) {
                urlBuilder.append("?");
                first = false;
            } else {
                urlBuilder.append("&");
            }
            urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        urlBuilder.append("&sign=").append(sign);

        return urlBuilder.toString();
    }

    /**
     * 验证签名是否正确（用于调试）
     *
     * @param params    请求参数（包括timestamp, nonce, 但不包括sign）
     * @param secretKey 密钥
     * @param sign      待验证的签名
     * @return 是否验证通过
     */
    public static boolean verifySignature(Map<String, String> params, String secretKey, String sign) {
        // 使用TreeMap保证排序
        Map<String, String> sortedParams = new TreeMap<>(params);

        // 构建待签名字符串
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        // 添加密钥
        sb.append("&key=").append(secretKey);

        // 生成签名
        String signStr = sb.toString();
        System.out.println("Verify Sign String: " + signStr);
        String generatedSign = DigestUtils.md5DigestAsHex(signStr.getBytes(StandardCharsets.UTF_8));
        System.out.println("Generated Sign: " + generatedSign);
        System.out.println("Provided Sign: " + sign);
        System.out.println("Sign Match: " + generatedSign.equalsIgnoreCase(sign));

        return generatedSign.equalsIgnoreCase(sign);
    }

    /**
     * 为POST表单请求生成签名（MD5算法）
     *
     * @param params    表单参数
     * @param secretKey 密钥
     * @return 签名值
     */
    public static String generatePostFormSignature(Map<String, String> params, String secretKey) {
        // 添加签名所需参数
        long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString().replace("-", "");

        params.put("timestamp", String.valueOf(timestamp));
        params.put("nonce", nonce);

        return generateGetSignature(params, secretKey);
    }

    /**
     * 为POST表单请求生成完整参数和签名
     *
     * @param params    表单参数
     * @param secretKey 密钥
     * @return 包含签名的参数Map
     */
    public static Map<String, String> generatePostFormSignatureParams(Map<String, String> params, String secretKey) {
        // 添加签名所需参数
        long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString().replace("-", "");

        params.put("timestamp", String.valueOf(timestamp));
        params.put("nonce", nonce);

        String sign = generateGetSignature(params, secretKey);
        params.put("sign", sign);

        return params;
    }

    /**
     * 为POST JSON请求生成签名（MD5算法）
     *
     * @param urlParams URL参数
     * @param body      JSON请求体
     * @param secretKey 密钥
     * @return 签名值
     */
    public static String generatePostJsonSignature(Map<String, String> urlParams, String body, String secretKey) {
        // 添加签名所需参数
        long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString().replace("-", "");

        urlParams.put("timestamp", String.valueOf(timestamp));
        urlParams.put("nonce", nonce);

        // 使用TreeMap保证排序
        Map<String, String> sortedParams = new TreeMap<>(urlParams);

        // 构建待签名字符串
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        // 添加请求体
        if (body != null && !body.isEmpty()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append("body=").append(body);
        }

        // 添加密钥
        sb.append("&key=").append(secretKey);

        // 生成签名
        String signStr = sb.toString();
        System.out.println("Sign String: " + signStr);
        return DigestUtils.md5DigestAsHex(signStr.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 为POST JSON请求生成完整URL和签名
     *
     * @param baseUrl   基础URL
     * @param urlParams URL参数
     * @param body      JSON请求体
     * @param secretKey 密钥
     * @return 带签名参数的URL
     */
    public static String generatePostJsonSignatureUrl(String baseUrl, Map<String, String> urlParams, String body, String secretKey) {
        // 添加签名所需参数
        long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString().replace("-", "");

        urlParams.put("timestamp", String.valueOf(timestamp));
        urlParams.put("nonce", nonce);

        // 使用TreeMap保证排序
        Map<String, String> sortedParams = new TreeMap<>(urlParams);

        // 生成签名
        String sign = generatePostJsonSignature(sortedParams, body, secretKey);

        // 构建完整URL（注意参数顺序要和签名时一致）
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl);
        boolean first = true;
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (first) {
                urlBuilder.append("?");
                first = false;
            } else {
                urlBuilder.append("&");
            }
            urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        urlBuilder.append("&sign=").append(sign);

        return urlBuilder.toString();
    }

    /**
     * 为POST JSON请求生成URL参数和签名
     *
     * @param urlParams URL参数
     * @param body      JSON请求体
     * @param secretKey 密钥
     * @return 包含签名的URL参数
     */
    public static Map<String, String> generatePostJsonSignatureParams(Map<String, String> urlParams, String body, String secretKey) {
        // 添加签名所需参数
        long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString().replace("-", "");

        urlParams.put("timestamp", String.valueOf(timestamp));
        urlParams.put("nonce", nonce);

        String sign = generatePostJsonSignature(urlParams, body, secretKey);
        urlParams.put("sign", sign);

        return urlParams;
    }

    /**
     * 使用HMAC-SHA256算法为GET请求生成签名
     *
     * @param params    请求参数
     * @param secretKey 密钥
     * @return 签名值
     */
    public static String generateGetSignatureHmacSha256(Map<String, String> params, String secretKey) {
        // 添加签名所需参数
        long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString().replace("-", "");

        params.put("timestamp", String.valueOf(timestamp));
        params.put("nonce", nonce);

        // 使用TreeMap保证排序
        Map<String, String> sortedParams = new TreeMap<>(params);

        // 构建待签名字符串
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
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
            throw new RuntimeException("生成签名失败", e);
        }
    }

    /**
     * 使用HMAC-SHA256算法为POST表单请求生成签名
     *
     * @param params    表单参数
     * @param secretKey 密钥
     * @return 签名值
     */
    public static String generatePostFormSignatureHmacSha256(Map<String, String> params, String secretKey) {
        // 添加签名所需参数
        long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString().replace("-", "");

        params.put("timestamp", String.valueOf(timestamp));
        params.put("nonce", nonce);

        return generateGetSignatureHmacSha256(params, secretKey);
    }

    /**
     * 使用HMAC-SHA256算法为POST JSON请求生成签名
     *
     * @param urlParams URL参数
     * @param body      JSON请求体
     * @param secretKey 密钥
     * @return 签名值
     */
    public static String generatePostJsonSignatureHmacSha256(Map<String, String> urlParams, String body, String secretKey) {
        // 添加签名所需参数
        long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString().replace("-", "");

        urlParams.put("timestamp", String.valueOf(timestamp));
        urlParams.put("nonce", nonce);

        // 使用TreeMap保证排序
        Map<String, String> sortedParams = new TreeMap<>(urlParams);

        // 构建待签名字符串
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        // 添加请求体
        if (body != null && !body.isEmpty()) {
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
            throw new RuntimeException("生成签名失败", e);
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

    public static void main(String[] args) {
        System.out.println("=== 所有请求方式的签名示例 ===\n");

        // 1. GET请求签名示例
        System.out.println("1. GET请求签名示例:");
        Map<String, String> getParams = new TreeMap<>();
        getParams.put("logLevel", "info");
        String getUrl = generateGetSignatureUrl("http://localhost:8080/api/log/updateLevel", getParams, "your_secret_key_here");
        System.out.println("GET请求完整URL: " + getUrl);

        // 2. POST表单请求签名示例
        System.out.println("\n2. POST表单请求签名示例:");
        Map<String, String> postFormParams = new TreeMap<>();
        postFormParams.put("username", "testuser");
        postFormParams.put("password", "password123");
        Map<String, String> signedPostFormParams = generatePostFormSignatureParams(postFormParams, "your_secret_key_here");
        System.out.println("POST表单请求参数: " + signedPostFormParams);
        System.out.println("请求URL: http://localhost:8080/api/login");
        System.out.println("请求方式: POST");
        System.out.println("Content-Type: application/x-www-form-urlencoded");

        // 3. POST JSON请求签名示例
        System.out.println("\n3. POST JSON请求签名示例:");
        Map<String, String> postJsonUrlParams = new TreeMap<>();
        String jsonBody = "{\"name\":\"张三\",\"age\":25,\"email\":\"zhangsan@example.com\"}";
        Map<String, String> signedPostJsonParams = generatePostJsonSignatureParams(postJsonUrlParams, jsonBody, "your_secret_key_here");
        System.out.println("POST JSON请求URL参数: " + signedPostJsonParams);
        System.out.println("请求URL: http://localhost:8080/api/user");
        System.out.println("请求方式: POST");
        System.out.println("Content-Type: application/json");
        System.out.println("请求体: " + jsonBody);

        // 4. 使用HMAC-SHA256算法的GET请求签名示例
        System.out.println("\n4. 使用HMAC-SHA256算法的GET请求签名示例:");
        Map<String, String> hmacParams = new TreeMap<>();
        hmacParams.put("userId", "12345");
        hmacParams.put("action", "getUserInfo");
        String hmacSign = generateGetSignatureHmacSha256(hmacParams, "your_secret_key_here");
        System.out.println("HMAC-SHA256签名: " + hmacSign);
        System.out.println("请求URL: http://localhost:8080/api/user/info?userId=12345&action=getUserInfo&timestamp=" +
                hmacParams.get("timestamp") + "&nonce=" + hmacParams.get("nonce") + "&sign=" + hmacSign);
    }
}