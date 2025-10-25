package com.example.gara_management.dto.BaoCaoThongKeDTO;

import java.math.BigDecimal;
import lombok.*;

// [TÍNH NĂNG] Thống kê Hóa Đơn
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonThongKeDTO {
    private Long tongSoHoaDon;
    private Long soHoaDonDaThanhToan;
    private BigDecimal tongDoanhThuDaThanhToan;
}

