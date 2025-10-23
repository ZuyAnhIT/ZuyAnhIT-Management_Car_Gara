package com.example.gara_management.dto.DichVuDTO;

import com.example.gara_management.model.DichVu;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DichVuResponseDTO {
    
    private Integer maDichVu;
    private String tenDichVu;
    private String moTa;
    private String anhDichVu; // Lưu TÊN FILE (UUID)
    private String anhDichVuUrl; // <-- Trường tiện ích để client hiển thị
    private Integer soLuongTon;
    private Integer soLuongBan;
    private BigDecimal gia;
    private Integer thoiGianUocTinh;
    private String trangThai;
    private LocalDateTime ngayTao;
    private String tenLoaiDichVu; 

    // Constructor tiện ích để chuyển từ Entity sang DTO
    public DichVuResponseDTO(DichVu dichVu) {
        this.maDichVu = dichVu.getMaDichVu();
        this.tenDichVu = dichVu.getTenDichVu();
        this.moTa = dichVu.getMoTa();
        this.anhDichVu = dichVu.getAnhDichVu(); // Tên file (UUID)
        
        // Tạo URL đầy đủ cho client
        if (dichVu.getAnhDichVu() != null) {
            this.anhDichVuUrl = "/uploads/images/" + dichVu.getAnhDichVu();
        }
        
        this.soLuongTon = dichVu.getSoLuongTon();
        this.soLuongBan = dichVu.getSoLuongBan();
        this.gia = dichVu.getGia();
        this.thoiGianUocTinh = dichVu.getThoiGianUocTinh();
        this.trangThai = dichVu.getTrangThai();
        this.ngayTao = dichVu.getNgayTao();
        
        if (dichVu.getLoaiDichVu() != null) {
             this.tenLoaiDichVu = dichVu.getLoaiDichVu().getTenLoai();
        }
    }
}