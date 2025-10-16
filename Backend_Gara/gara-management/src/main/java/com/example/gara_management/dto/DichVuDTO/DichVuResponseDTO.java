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
    private String anhDichVu;
    private Integer soLuongTon;
    private Integer soLuongBan;
    private BigDecimal gia;
    private Integer thoiGianUocTinh;
    private String trangThai;
    private LocalDateTime ngayTao;
    
    // Trường hiển thị Tên Loại Dịch Vụ thay vì Mã
    private String tenLoaiDichVu; 

    // Constructor tiện ích để chuyển từ Entity sang DTO
    public DichVuResponseDTO(DichVu dichVu) {
        this.maDichVu = dichVu.getMaDichVu();
        this.tenDichVu = dichVu.getTenDichVu();
        this.moTa = dichVu.getMoTa();
        this.anhDichVu = dichVu.getAnhDichVu();
        this.soLuongTon = dichVu.getSoLuongTon();
        this.soLuongBan = dichVu.getSoLuongBan();
        this.gia = dichVu.getGia();
        this.thoiGianUocTinh = dichVu.getThoiGianUocTinh();
        this.trangThai = dichVu.getTrangThai();
        this.ngayTao = dichVu.getNgayTao();
        
        // Lấy Tên Loại Dịch Vụ từ mối quan hệ Many-to-One
        // Kiểm tra null để an toàn nếu LoaiDichVu chưa được tải (dù đã đặt nullable=false trong model)
        if (dichVu.getLoaiDichVu() != null) {
             this.tenLoaiDichVu = dichVu.getLoaiDichVu().getTenLoai();
        } else {
             this.tenLoaiDichVu = "Không xác định";
        }
    }
}