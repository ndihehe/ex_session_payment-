package org.example.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EnvUtil {

    private static final Map<String, String> envMap = new HashMap<>();
    private static boolean loaded = false;

    static {
        loadEnvFile();
    }

    private static synchronized void loadEnvFile() {
        if (loaded) return;

        // Các vị trí tìm kiếm file .env
        String[] possiblePaths = {
            ".env",
            "../.env",
            System.getProperty("user.dir") + File.separator + ".env",
            "D:\\Projects\\ex_session_tracking\\.env"
        };

        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                try (InputStream input = new FileInputStream(file)) {
                    Properties props = new Properties();
                    props.load(input);
                    for (String name : props.stringPropertyNames()) {
                        envMap.put(name.trim(), props.getProperty(name).trim());
                    }
                    System.out.println("[EnvUtil] Đã tải biến môi trường từ: " + file.getAbsolutePath());
                    loaded = true;
                    return;
                } catch (Exception e) {
                    System.err.println("[EnvUtil] Lỗi đọc file .env tại " + path + ": " + e.getMessage());
                }
            }
        }
        loaded = true;
    }

    public static String get(String key) {
        return get(key, null);
    }

    public static String get(String key, String defaultValue) {
        if (key == null) return defaultValue;

        // 1. Ưu tiên biến môi trường hệ thống (Render, Docker, System Environment)
        String val = System.getenv(key);
        if (val != null && !val.trim().isEmpty()) {
            return val.trim();
        }

        // 2. Kiểm tra System Property (-Dkey=value)
        val = System.getProperty(key);
        if (val != null && !val.trim().isEmpty()) {
            return val.trim();
        }

        // 3. Kiểm tra trong file .env đã nạp
        val = envMap.get(key);
        if (val != null && !val.trim().isEmpty()) {
            return val.trim();
        }

        return defaultValue;
    }
}
