package com.example.gara_management.controller;

import com.example.gara_management.dto.ApiResponse;
import com.example.gara_management.dto.auth.*;
import com.example.gara_management.model.TaiKhoan;
import com.example.gara_management.service.AuthService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "Auth", description = "API đăng nhập, đăng ký.....")

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    // Đăng nhập
    @PostMapping("/dangNhap")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Tên đăng nhập hoặc mật khẩu không đúng"));
        }
    }
    
    // Đăng ký
    @PostMapping("/dangKy")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Đăng ký tài khoản thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Lỗi: " + e.getMessage()));
        }
    }
    
    // Đổi mật khẩu
    @PutMapping("/doiMatKhau")
    public ResponseEntity<ApiResponse<String>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            String username = authentication.getName();
            authService.changePassword(username, request);
            return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Lỗi: " + e.getMessage()));
        }
    }
    // ====================== THỐNG KÊ TÀI KHOẢN ======================
    @GetMapping("/thongKeTaiKhoan")
    public ResponseEntity<Map<String, Long>> thongKeTaiKhoan() {
        Map<String, Long> result = authService.thongKeTaiKhoan();
        return ResponseEntity.ok(result);
    }
}
