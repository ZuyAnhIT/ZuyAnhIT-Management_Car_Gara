package com.example.gara_management.dto.ThoDTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThoStatisticsDTO {
    private long totalTho;
    private long kinhNghiemCao;  // > 3 năm
    private long kinhNghiemThap; // <= 3 năm
}