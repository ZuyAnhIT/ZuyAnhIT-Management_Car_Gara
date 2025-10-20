package com.example.gara_management.dto.BaoCaoDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TongTonKhoDTO {
    
    private Long tongSoLuongTon;
    private String message;
    
    // Constructor tiện ích để tạo response thành công
    public static TongTonKhoDTO createSuccessResponse(Long tongSoLuongTon) {
        return TongTonKhoDTO.builder()
                .tongSoLuongTon(tongSoLuongTon)
                .message("Lấy tổng số lượng tồn kho thành công")
                .build();
    }
    
    // Constructor tiện ích để tạo response khi không có dữ liệu
    public static TongTonKhoDTO createEmptyResponse() {
        return TongTonKhoDTO.builder()
                .tongSoLuongTon(0L)
                .message("Không có dịch vụ nào trong kho")
                .build();
    }
}