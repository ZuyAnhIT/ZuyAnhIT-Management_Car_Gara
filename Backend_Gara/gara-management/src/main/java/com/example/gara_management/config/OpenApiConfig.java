package com.example.gara_management.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
@SecurityScheme(
    name = "bearerAuth", // Tên tham chiếu
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer", // <-- PHẢI LÀ 'bearer' (viết thường)
    description = "Nhập JWT Token."
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("Gara Management API")
                           .version("1.0")
                           .description("Tài liệu API cho hệ thống quản lý Gara."));
    }
}