package com.example.gara_management.dto.DichVuDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DichVuStatisticsDTO {
    private long totalDichVu;
    private long sapHet;
    private long conHang;
    private long hetHang;
    private long tongSoLuongTon;
    private BigDecimal tongGiaTriTonKho;
}
