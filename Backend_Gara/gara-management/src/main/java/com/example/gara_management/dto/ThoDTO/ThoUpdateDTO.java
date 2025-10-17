package com.example.gara_management.dto.ThoDTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThoUpdateDTO {
    private String tenTho;
    private String chuyenMon;
    private String soDienThoai;
    private String email;
    private Integer kinhNghiem;
    private String trangThai;
}
