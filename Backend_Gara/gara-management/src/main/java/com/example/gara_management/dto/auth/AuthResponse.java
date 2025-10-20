package com.example.gara_management.dto.auth;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Integer maTaiKhoan;
    private String tenDangNhap;
    private String email;
    private String vaiTro;
    private String trangThai;
}

