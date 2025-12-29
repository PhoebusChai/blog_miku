package com.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload.path:uploads}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的访问路径
        // 由于 context-path 是 /api，所以实际访问路径是 /api/uploads/**
        // 前端通过代理 /uploads -> http://localhost:8080/uploads 访问
        // 这里需要去掉 context-path 的影响，直接映射 /uploads/**
        File uploadDir = new File(uploadPath);
        String absolutePath = uploadDir.getAbsolutePath();
        
        // 确保路径以分隔符结尾
        if (!absolutePath.endsWith(File.separator)) {
            absolutePath = absolutePath + File.separator;
        }
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath);
    }
}
