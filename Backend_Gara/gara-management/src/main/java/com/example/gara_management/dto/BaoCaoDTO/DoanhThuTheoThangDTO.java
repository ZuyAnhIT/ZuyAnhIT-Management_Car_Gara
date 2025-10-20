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
public class DoanhThuTheoThangDTO {
    private List<DoanhThuTuan> doanhThuTheoTuan;
    private BigDecimal tongDoanhThuThang;
    private String message;
    private String thang;
    private String nam;
    private Integer soTuan;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DoanhThuTuan {
        private Integer tuan;                    // Tuần thứ mấy trong tháng (1, 2, 3, 4, 5, 6)
        private String tuTuan;                   // Ngày đầu tuần
        private String denTuan;                  // Ngày cuối tuần
        private BigDecimal doanhThu;            // Doanh thu của tuần
        private Integer soHoaDon;               // Số lượng hóa đơn của tuần
        private String moTaTuan;                // Mô tả tuần (VD: "Tuần 1", "Tuần 2")
    }

    public static DoanhThuTheoThangDTO createSuccessResponse(List<DoanhThuTuan> doanhThuTheoTuan, 
                                                              BigDecimal tongDoanhThuThang,
                                                              String thang, String nam, Integer soTuan) {
        return DoanhThuTheoThangDTO.builder()
                .doanhThuTheoTuan(doanhThuTheoTuan)
                .tongDoanhThuThang(tongDoanhThuThang)
                .message("Lấy doanh thu theo tháng thành công")
                .thang(thang)
                .nam(nam)
                .soTuan(soTuan)
                .build();
    }

    public static DoanhThuTheoThangDTO createEmptyResponse(String thang, String nam) {
        return DoanhThuTheoThangDTO.builder()
                .doanhThuTheoTuan(List.of())
                .tongDoanhThuThang(BigDecimal.ZERO)
                .message("Không có hóa đơn nào đã thanh toán trong tháng " + thang + "/" + nam)
                .thang(thang)
                .nam(nam)
                .soTuan(0)
                .build();
    }
}
