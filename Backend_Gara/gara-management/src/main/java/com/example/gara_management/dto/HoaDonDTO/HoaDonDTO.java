package com.example.gara_management.dto.HoaDonDTO;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.gara_management.dto.PhieuSuaChuaDTO.ChiTietPhieuSuaChuaDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonDTO {
    private Integer maHoaDon;
    private Integer maPhieu;
    private LocalDateTime ngayLapHoaDon;
    private LocalDateTime thoiGianThanhCong;
    private String kieuThanhToan;
    private String trangThai;
    private BigDecimal tongTien;

    private List<ChiTietPhieuSuaChuaDTO> chiTietList;
}