package com.example.gara_management.dto.BaoCaoThongKeDTO;

import lombok.*;

// [TÍNH NĂNG] Top 5 Dịch Vụ Sử Dụng Nhiều
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopDichVuDTO {
    private Integer maDichVu;
    private String tenDichVu;
    private Long soLuongSuDung;
}

