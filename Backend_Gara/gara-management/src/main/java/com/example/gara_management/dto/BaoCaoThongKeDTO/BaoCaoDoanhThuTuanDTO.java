package com.example.gara_management.dto.BaoCaoThongKeDTO;

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
public class BaoCaoDoanhThuTuanDTO {
    private int nam;
    private int tuan;
    private BigDecimal tongDoanhThu;
    private Map<String, BigDecimal> chiTietTheoNgay;
}