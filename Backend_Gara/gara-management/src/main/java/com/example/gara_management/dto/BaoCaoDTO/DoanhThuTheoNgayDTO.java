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
public class DoanhThuTheoNgayDTO {
    private List<DoanhThuNgay> doanhThuTheoNgay;
    private BigDecimal tongDoanhThuTuan;
    private String message;
    private String tuTuan;
    private String denTuan;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DoanhThuNgay {
        private String tenNgay;        // "Thứ 2", "Thứ 3", ...
        private String ngay;          // "2024-10-14"
        private BigDecimal doanhThu;  // Doanh thu của ngày đó
        private Integer soHoaDon;     // Số lượng hóa đơn của ngày đó
    }

    public static DoanhThuTheoNgayDTO createSuccessResponse(List<DoanhThuNgay> doanhThuTheoNgay, 
                                                             BigDecimal tongDoanhThuTuan,
                                                             String tuTuan, String denTuan) {
        return DoanhThuTheoNgayDTO.builder()
                .doanhThuTheoNgay(doanhThuTheoNgay)
                .tongDoanhThuTuan(tongDoanhThuTuan)
                .message("Lấy doanh thu theo từng ngày trong tuần thành công")
                .tuTuan(tuTuan)
                .denTuan(denTuan)
                .build();
    }

    public static DoanhThuTheoNgayDTO createEmptyResponse(String tuTuan, String denTuan) {
        return DoanhThuTheoNgayDTO.builder()
                .doanhThuTheoNgay(List.of())
                .tongDoanhThuTuan(BigDecimal.ZERO)
                .message("Không có hóa đơn nào đã thanh toán trong tuần từ " + tuTuan + " đến " + denTuan)
                .tuTuan(tuTuan)
                .denTuan(denTuan)
                .build();
    }
}
