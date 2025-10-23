package com.example.gara_management.config;

import com.example.gara_management.util.FileUploadUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    /**
     * Cấu hình để frontend có thể truy cập ảnh qua URL: http://localhost:8080/uploads/images/{tên_file}.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ánh xạ URL /uploads/images/** tới thư mục vật lý
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:///" + FileUploadUtil.getUploadPath().toString().replace("\\", "/") + "/");
    }
}