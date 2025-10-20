package com.example.gara_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.example.gara_management.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Giữ nguyên để dùng @PreAuthorize trên các phương thức Service/Controller
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    // --- SECURITY FILTER CHAIN  ---
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable()) // Vô hiệu hóa CORS trong config để xử lý qua Filter (hoặc cho phép)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        
                        // 1. PUBLIC ENDPOINTS: Auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/loaidichvu/**","/api/tho/**","/api/khachhang/**","/api/xe/**","/api/phieusuachua/**","/api/hoadon/**","/api/dichvu/**").permitAll()
                        // 2. PUBLIC ENDPOINTS: SWAGGER/OPENAPI
                        .requestMatchers(
                            "/v2/api-docs",
                            "/v3/api-docs",
                            "/v3/api-docs/**",
                            "/swagger-resources",
                            "/swagger-resources/**",
                            "/configuration/ui",
                            "/configuration/security",
                            "/swagger-ui/**",
                            "/swagger-ui.html", // Đường dẫn cũ
                            "/webjars/**"
                        ).permitAll()
                        
                        // // 3. ADMIN/ROLE ENDPOINTS
                        // // Lưu ý: Spring Security sử dụng tiền tố "ROLE_" khi kiểm tra hasRole()
                        // // Nên trong DB/Code: VaiTro="QUẢN LÝ" -> Kiểm tra bằng hasRole("QUẢN LÝ")
                        // .requestMatchers("/api/thongke/**").hasAnyRole("Quản lý")
                        
                        // 4. CÁC ENDPOINT CÒN LẠI YÊU CẦU XÁC THỰC
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}