package com.example.gara_management.dto.BaoCaoThongKeDTO;

import java.math.BigDecimal;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhieuSuaChuaThongKeDTO {
    private Long soPhieuDaGiao;
    private Long soPhieuDangSua;
    private Long soPhieuChoXuLy;
    private BigDecimal tongDoanhThu;
}
