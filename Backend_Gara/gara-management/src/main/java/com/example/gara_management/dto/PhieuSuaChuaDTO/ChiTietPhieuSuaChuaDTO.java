package com.example.gara_management.dto.PhieuSuaChuaDTO;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietPhieuSuaChuaDTO {
    private Integer maDichVu;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private String tenDichVu;
}
