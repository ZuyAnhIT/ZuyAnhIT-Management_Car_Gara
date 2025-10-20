package com.example.gara_management.dto.BaoCaoThongKeDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TiLeSuDungLoaiDichVuDTO {
    private String tenLoaiDichVu;
    private long soLanSuDung;
    private double tiLeSuDung; // Tỉ lệ dưới dạng phần trăm, ví dụ: 45.5
}