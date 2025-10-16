package com.example.gara_management.dto.KhachHangDTO;
import com.example.gara_management.model.KhachHang;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class KhachHangResponseDTO {
    private Integer maKhachHang;
    private String tenKhachHang;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private String loaiKhach;
    private String ghiChu;
    private String trangThai;
    private LocalDateTime ngayTao;

    public KhachHangResponseDTO(KhachHang khachHang) {
        this.maKhachHang = khachHang.getMaKhachHang();
        this.tenKhachHang = khachHang.getTenKhachHang();
        this.soDienThoai = khachHang.getSoDienThoai();
        this.email = khachHang.getEmail();
        this.diaChi = khachHang.getDiaChi();
        this.loaiKhach = khachHang.getLoaiKhach();
        this.ghiChu = khachHang.getGhiChu();
        this.trangThai = khachHang.getTrangThai();
        this.ngayTao = khachHang.getNgayTao();
    }
}
