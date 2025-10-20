package com.example.gara_management.dto.BaoCaoThongKeDTO; // <-- KIỂM TRA KỸ DÒNG NÀY

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaoCaoDoanhThuNamDTO {
    private int nam;
    private BigDecimal tongDoanhThu;
    private Map<String, BigDecimal> chiTietTheoQuy;
}