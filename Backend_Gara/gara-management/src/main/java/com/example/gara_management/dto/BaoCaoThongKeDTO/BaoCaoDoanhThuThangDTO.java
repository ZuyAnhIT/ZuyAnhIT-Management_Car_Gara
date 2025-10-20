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
public class BaoCaoDoanhThuThangDTO {
    private int nam;
    private int thang;
    private BigDecimal tongDoanhThu;
    // Key ví dụ: "Tuần 42 (14/10 - 20/10)", Value: Doanh thu của tuần đó
    private Map<String, BigDecimal> chiTietTheoTuan;
}