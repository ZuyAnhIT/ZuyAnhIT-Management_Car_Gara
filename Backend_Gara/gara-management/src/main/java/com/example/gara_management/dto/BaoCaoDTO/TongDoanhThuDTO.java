package com.example.gara_management.dto.BaoCaoDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TongDoanhThuDTO {
    
    private BigDecimal tongDoanhThu;
    private String message;
    
    // Constructor tiện ích để tạo response thành công
    public static TongDoanhThuDTO createSuccessResponse(BigDecimal tongDoanhThu) {
        return TongDoanhThuDTO.builder()
                .tongDoanhThu(tongDoanhThu)
                .message("Lấy tổng doanh thu thành công")
                .build();
    }
    
    // Constructor tiện ích để tạo response khi không có dữ liệu
    public static TongDoanhThuDTO createEmptyResponse() {
        return TongDoanhThuDTO.builder()
                .tongDoanhThu(BigDecimal.ZERO)
                .message("Không có hóa đơn nào đã thanh toán")
                .build();
    }
}