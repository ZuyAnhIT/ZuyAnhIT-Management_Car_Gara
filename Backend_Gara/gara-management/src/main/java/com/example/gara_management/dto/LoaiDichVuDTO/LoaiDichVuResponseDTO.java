package com.example.gara_management.dto.LoaiDichVuDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.example.gara_management.model.LoaiDichVu;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoaiDichVuResponseDTO {
    
    private Integer maLoai;
    private String tenLoai;
    private String trangThai;
    private LocalDateTime ngayTao;

    // Constructor tiện ích để chuyển từ Entity sang DTO
    public LoaiDichVuResponseDTO(LoaiDichVu loaiDichVu) {
        this.maLoai = loaiDichVu.getMaLoai();
        this.tenLoai = loaiDichVu.getTenLoai();
        this.trangThai = loaiDichVu.getTrangThai();
        this.ngayTao = loaiDichVu.getNgayTao();
    }
}