package com.example.gara_management.dto.ThoDTO;

import com.example.gara_management.model.Tho;
import java.time.LocalDateTime;
import lombok.*;
public class ThoResponseDTO {
    private Integer maTho;
    private String tenTho;
    private String chuyenMon;
    private String soDienThoai;
    private String email;
    private Integer kinhNghiem;
    private String trangThai;
    private LocalDateTime ngayVaoLam;

    private ThoResponseDTO(Tho tho){
        this.maTho = tho.getMaTho();
        this.tenTho = tho.getTenTho();
        this.chuyenMon = tho.getChuyenMon();
        this.soDienThoai = tho.getSoDienThoai();
        this.email = tho.getEmail();
        this.kinhNghiem = tho.getKinhNghiem();
        this.trangThai = tho.getTrangThai();
        this.ngayVaoLam = tho.getNgayVaoLam();

    }
}
