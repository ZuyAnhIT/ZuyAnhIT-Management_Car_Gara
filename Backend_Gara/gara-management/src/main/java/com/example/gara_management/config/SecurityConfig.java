package com.example.gara_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt CSRF (thường cần cho REST API)
            .authorizeHttpRequests(auth -> auth
                // MỞ FULL QUYỀN TRUY CẬP: Cho phép tất cả các request
                .anyRequest().permitAll() // <-- THAY ĐỔI QUAN TRỌNG NHẤT 
            )
            // Bỏ các cấu hình xác thực như .sessionManagement, .addFilterBefore, v.v. 
            // nếu bạn không muốn chúng can thiệp.
            ;
        return http.build();
    }

    // Các Bean khác như PasswordEncoder, AuthenticationProvider... 
    // vẫn có thể được giữ lại nếu cần cho các service nội bộ khác
}