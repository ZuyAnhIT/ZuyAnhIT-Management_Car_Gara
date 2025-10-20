package com.example.gara_management.dto.BaoCaoThongKeDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThongKeDTO {

    private Long tongSoDichVu;
    private Long tongSoLuongTon;
    private Long tongSoTho;
    private Long tongSoLoaiDichVu;
    private Long tongSoKhachHang;
    private Long tongSoHoaDonDaThanhToan;
}