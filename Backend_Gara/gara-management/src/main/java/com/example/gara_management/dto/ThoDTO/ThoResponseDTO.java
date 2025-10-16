package com.example.gara_management.dto.ThoDTO;

import com.example.gara_management.model.Tho;
import java.time.LocalDateTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThoResponseDTO {
    private Integer maTho;
    private String tenTho;
    private String chuyenMon;
    private String soDienThoai;
    private String email;
    private String trangThai;
    private Integer kinhNghiem;
    private LocalDateTime ngayVaoLam;

    private ThoResponseDTO(Tho tho){
        this.maTho = tho.getMaTho();
        this.tenTho = tho.getTenTho();
        this.chuyenMon = tho.getChuyenMon();
        this.soDienThoai = tho.getSoDienThoai();
        this.email = tho.getEmail();
        this.trangThai = tho.getTrangThai();
        this.kinhNghiem = tho.getKinhNghiem();
        this.ngayVaoLam = tho.getNgayVaoLam();

    }
}
