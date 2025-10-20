package com.example.gara_management.dto.BaoCaoDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoanhThuTheoNamDTO {
    private List<DoanhThuQuy> doanhThuTheoQuy;
    private BigDecimal tongDoanhThuNam;
    private String message;
    private String nam;
    private Integer soQuy;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DoanhThuQuy {
        private Integer quy;                     // Quý thứ mấy (1, 2, 3, 4)
        private String tuThang;                 // Tháng đầu quý
        private String denThang;                // Tháng cuối quý
        private BigDecimal doanhThu;            // Doanh thu của quý
        private Integer soHoaDon;               // Số lượng hóa đơn của quý
        private String moTaQuy;                 // Mô tả quý (VD: "Quý 1", "Quý 2")
    }

    public static DoanhThuTheoNamDTO createSuccessResponse(List<DoanhThuQuy> doanhThuTheoQuy, 
                                                             BigDecimal tongDoanhThuNam,
                                                             String nam, Integer soQuy) {
        return DoanhThuTheoNamDTO.builder()
                .doanhThuTheoQuy(doanhThuTheoQuy)
                .tongDoanhThuNam(tongDoanhThuNam)
                .message("Lấy doanh thu theo năm thành công")
                .nam(nam)
                .soQuy(soQuy)
                .build();
    }

    public static DoanhThuTheoNamDTO createEmptyResponse(String nam) {
        return DoanhThuTheoNamDTO.builder()
                .doanhThuTheoQuy(List.of())
                .tongDoanhThuNam(BigDecimal.ZERO)
                .message("Không có hóa đơn nào đã thanh toán trong năm " + nam)
                .nam(nam)
                .soQuy(0)
                .build();
    }
}
