package com.example.gara_management.dto.auth;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoanUpdateDTO {
    private String tenDangNhap;
    private String matKhau;
    private String email;
    private String vaiTro;
    private String trangThai;
}