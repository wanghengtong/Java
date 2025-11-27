package com.wanghengtong.framework.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FileReaderUtils {
    private static final long maxFileSize = 1024 * 1024 * 1024;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final LoadingCache<String, FileCache> fileCache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(60, TimeUnit.MINUTES).build(new CacheLoader<String, FileCache>() {
        @Override
        public FileCache load(String filePath) throws Exception {
            return createFileCache(new File(filePath));
        }
    });

    @Data
    private static class FileCache {
        private long lastModified;
        private JsonNode cachedJsonArray;
        private List<String> cachedLines;
        private int totalElements;
        private boolean isJsonArray;
        private long expireTime;
    }

    @Data
    public static class PageFileResult<T> {
        private int page;
        private int size;
        private List<T> data;
        private int totalElements;
        private int totalPages;

        public PageFileResult(int page, int size, List<T> data, int totalElements, int totalPages) {
            this.page = page;
            this.size = size;
            this.data = data;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }
    }

    public static <T> PageFileResult<T> readPage(String relativePath, int page, int size) throws IOException, ExecutionException {
        File file = validateAndGetFile(relativePath);
        int actualPage = Math.max(page, 1);

        // 从Guava Cache中获取缓存
        FileCache cache = fileCache.get(file.getAbsolutePath());

        // 检查文件是否被修改，如果被修改，则刷新缓存
        if (cache.getLastModified() != file.lastModified()) {
            // 文件被修改，刷新缓存
            fileCache.refresh(file.getAbsolutePath());
            cache = fileCache.get(file.getAbsolutePath());
        }

        if (cache.isJsonArray()) {
            return readJsonArrayFromCache(cache, actualPage, size);
        } else {
            return readJsonLinesFromCache(cache, actualPage, size);
        }
    }

    /**
     * 清空指定文件的缓存
     */
    public static void clearFileCache(String filePath) {
        log.info("清空缓存: {}", filePath);
        fileCache.invalidate(filePath);
    }

    /**
     * 验证文件路径安全性
     */
    private static File validateAndGetFile(String relativePath) throws FileNotFoundException {
        // 防止目录遍历攻击
        if (relativePath.contains("..")) {
            throw new SecurityException("文件路径包含非法字符");
        }
        File file = new File(relativePath);
        // 检查文件是否存在
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + relativePath);
        }
        // 检查文件大小
        if (file.length() > maxFileSize) {
            throw new IllegalArgumentException("文件大小超过限制");
        }
        return file;
    }

    private static <T> PageFileResult<T> readJsonArrayFromCache(FileCache cache, int page, int size) {
        JsonNode jsonArray = cache.getCachedJsonArray();
        int totalElements = cache.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        if (page > totalPages && totalPages > 0) {
            return new PageFileResult<>(page, size, new ArrayList<>(), totalElements, totalPages);
        }

        List<T> data = new ArrayList<>();
        int startIdx = (page - 1) * size;
        int endIdx = Math.min(startIdx + size, totalElements);

        for (int i = startIdx; i < endIdx; i++) {
            try {
                T obj = (T) jsonArray.get(i);
                if (obj != null) {
                    data.add(obj);
                }
            } catch (Exception e) {
                log.warn("解析JSON数组元素失败，索引: {}", i, e);
            }
        }

        return new PageFileResult<>(page, size, data, totalElements, totalPages);
    }

    private static <T> PageFileResult<T> readJsonLinesFromCache(FileCache cache, int page, int size) {
        List<String> lines = cache.getCachedLines();
        int totalElements = cache.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        if (page > totalPages && totalPages > 0) {
            return new PageFileResult<>(page, size, new ArrayList<>(), totalElements, totalPages);
        }

        List<T> data = new ArrayList<>();
        int startLine = (page - 1) * size;
        int endLine = Math.min(startLine + size, totalElements);

        for (int i = startLine; i < endLine; i++) {
            try {
                T obj = (T) objectMapper.readValue(lines.get(i), JsonNode.class);
                if (obj != null) {
                    data.add(obj);
                }
            } catch (Exception e) {
                log.warn("解析JSON行失败，行号: {}", i + 1, e);
            }
        }

        return new PageFileResult<>(page, size, data, totalElements, totalPages);
    }

    private static FileCache createFileCache(File file) throws IOException {
        FileCache cache = new FileCache();
        cache.setLastModified(file.lastModified());
        cache.setExpireTime(System.currentTimeMillis() + 1000 * 60 * 30);

        boolean isJsonArray = isJsonArrayFormat(file);
        cache.setJsonArray(isJsonArray);

        if (isJsonArray) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                JsonNode jsonArray = objectMapper.readTree(reader);
                cache.setCachedJsonArray(jsonArray);
                cache.setTotalElements(jsonArray.size());
            }
        } else {
            List<String> lines = new ArrayList<>();
            int count = 0;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        lines.add(line);
                        count++;
                    }
                }
            }
            cache.setCachedLines(lines);
            cache.setTotalElements(count);
        }
        return cache;
    }

    /**
     * 判断文件是否为JSON数组格式
     */
    private static boolean isJsonArrayFormat(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int ch;
            StringBuilder prefix = new StringBuilder();
            int count = 0;
            while ((ch = reader.read()) != -1 && count < 10) {
                char c = (char) ch;
                if (!Character.isWhitespace(c)) {
                    prefix.append(c);
                    count++;
                }
                if (prefix.toString().startsWith("[")) {
                    return true;
                }
                if (prefix.toString().startsWith("{")) {
                    return false;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String relativePath = "xxx.txt";
        try {
            PageFileResult<JsonNode> pageResult = FileReaderUtils.readPage(relativePath, 1, 10000);
            // System.out.println(pageResult);
            System.out.println("Page: " + pageResult.getPage());
            System.out.println("Size: " + pageResult.getSize());
            // System.out.println("Data: " + pageResult.getData());
            System.out.println("Total Pages: " + pageResult.getTotalPages());
            System.out.println("Total Elements: " + pageResult.getTotalElements());

            FileReaderUtils.clearFileCache(relativePath);
        } catch (IOException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}