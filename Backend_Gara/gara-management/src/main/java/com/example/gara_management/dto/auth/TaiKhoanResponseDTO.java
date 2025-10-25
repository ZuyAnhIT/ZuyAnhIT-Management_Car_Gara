package com.example.gara_management.dto.auth;

import com.example.gara_management.model.Tho;
import java.time.LocalDateTime;
import lombok.*;
import com.example.gara_management.model.TaiKhoan;

// TaiKhoanResponseDTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoanResponseDTO {
    private Integer maTaiKhoan;
    private String tenDangNhap;
    private String email;
    private String vaiTro;
    private String trangThai;
    private LocalDateTime ngayTao;

    public TaiKhoanResponseDTO(TaiKhoan taiKhoan) {
        this.maTaiKhoan = taiKhoan.getMaTaiKhoan();
        this.tenDangNhap = taiKhoan.getTenDangNhap();
        this.email = taiKhoan.getEmail();
        this.vaiTro = taiKhoan.getVaiTro();
        this.trangThai = taiKhoan.getTrangThai();
        this.ngayTao = taiKhoan.getNgayTao();
    }
}