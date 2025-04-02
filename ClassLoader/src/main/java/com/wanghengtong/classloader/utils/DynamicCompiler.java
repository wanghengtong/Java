package com.wanghengtong.classloader.utils;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class DynamicCompiler {
    public static void main(String[] args) throws Exception {
        // 要动态编译和执行的Java代码
        String code = "public class DynamicClass {\n" + "    public void execute() {\n" + "        System.out.println(\"Hello, Dynamic Code!\");\n" + "    }\n" + "}";

        // 动态编译并执行代码
        executeDynamicCode(code);
    }

    public static void executeDynamicCode(String code) throws Exception {
        // 1. 将代码写入临时文件
        File sourceFile = new File("DynamicClass.java");
        try (FileWriter writer = new FileWriter(sourceFile)) {
            writer.write(code);
        }

        // 2. 使用 JavaCompiler 编译代码
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int compilationResult = compiler.run(null, null, null, sourceFile.getPath());

        if (compilationResult != 0) {
            throw new RuntimeException("Compilation failed");
        }

        // 3. 加载编译后的类
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new File("").toURI().toURL()});
        Class<?> clazz = Class.forName("DynamicClass", true, classLoader);

        // 4. 创建实例并调用方法
        Object instance = clazz.getDeclaredConstructor().newInstance();
        Method method = clazz.getMethod("execute");
        method.invoke(instance);

        // 5. 清理临时文件
        sourceFile.delete();
        new File("DynamicClass.class").delete();
    }
}
