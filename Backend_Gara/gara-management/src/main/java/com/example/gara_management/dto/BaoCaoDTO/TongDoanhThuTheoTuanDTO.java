package com.example.gara_management.dto.BaoCaoDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TongDoanhThuTheoTuanDTO {
    private BigDecimal tongDoanhThu;
    private String message;
    private String tuTuan;
    private String denTuan;

    public static TongDoanhThuTheoTuanDTO createSuccessResponse(BigDecimal tongDoanhThu, String tuTuan, String denTuan) {
        return TongDoanhThuTheoTuanDTO.builder()
                .tongDoanhThu(tongDoanhThu)
                .message("Lấy tổng doanh thu theo tuần thành công")
                .tuTuan(tuTuan)
                .denTuan(denTuan)
                .build();
    }

    public static TongDoanhThuTheoTuanDTO createEmptyResponse(String tuTuan, String denTuan) {
        return TongDoanhThuTheoTuanDTO.builder()
                .tongDoanhThu(BigDecimal.ZERO)
                .message("Không có hóa đơn nào đã thanh toán trong tuần từ " + tuTuan + " đến " + denTuan)
                .tuTuan(tuTuan)
                .denTuan(denTuan)
                .build();
    }
}
