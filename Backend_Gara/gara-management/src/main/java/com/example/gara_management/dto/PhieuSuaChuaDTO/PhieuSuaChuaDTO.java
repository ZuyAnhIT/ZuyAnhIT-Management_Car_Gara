package com.example.gara_management.dto.PhieuSuaChuaDTO;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuSuaChuaDTO {
    private Integer maPhieu;
    private Integer maXe;
    private String bienSo;
    private Integer maTho;
    private String tenTho;
    private LocalDateTime ngayLap;
    private String moTa;
    private String trangThai;
    private BigDecimal tongTien;
    private List<ChiTietPhieuSuaChuaDTO> chiTietList;
}