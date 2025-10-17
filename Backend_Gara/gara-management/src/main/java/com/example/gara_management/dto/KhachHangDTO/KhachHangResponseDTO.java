package com.example.gara_management.dto.KhachHangDTO;

import com.example.gara_management.model.KhachHang;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangResponseDTO {

    private Integer maKhachHang;
    private String tenKhachHang;
    private String soDienThoai;
    private String email;
    private String trangThai;
    private String diaChi;
    private String loaiKhach;
    private String ghiChu;
    
    // Constructor tiện ích chuyển từ Entity sang DTO
    public KhachHangResponseDTO(KhachHang khachHang) {
        this.maKhachHang = khachHang.getMaKhachHang();
        this.tenKhachHang = khachHang.getTenKhachHang();
        this.soDienThoai = khachHang.getSoDienThoai();
        this.email = khachHang.getEmail();
        this.trangThai = khachHang.getTrangThai();
        this.diaChi = khachHang.getDiaChi();
        this.loaiKhach = khachHang.getLoaiKhach();
        this.ghiChu = khachHang.getGhiChu();
    }
}