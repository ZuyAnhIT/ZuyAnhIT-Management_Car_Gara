package com.example.gara_management.dto.BaoCaoDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TongKhachHangDTO {
    private Long tongKhachHang;
    private String message;

    public static TongKhachHangDTO createSuccessResponse(Long tongKhachHang) {
        return TongKhachHangDTO.builder()
                .tongKhachHang(tongKhachHang)
                .message("Lấy tổng số lượng khách hàng thành công")
                .build();
    }

    public static TongKhachHangDTO createEmptyResponse() {
        return TongKhachHangDTO.builder()
                .tongKhachHang(0L)
                .message("Không có khách hàng nào trong hệ thống")
                .build();
    }
}
