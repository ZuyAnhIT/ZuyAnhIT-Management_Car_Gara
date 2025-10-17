// File: src/main/java/com/example/gara_management/dto/XeResponseDTO.java
package com.example.gara_management.dto.XeDTO;

import com.example.gara_management.model.Xe;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class XeResponseDTO {

    private Integer maXe;
    private String bienSo;
    private String hangXe;
    private String dongXe;
    private Integer namSanXuat;
    private String mauSac;
    private String trangThai;
    private String tenKhachHang;
    private Integer maKhachHang;

    // Constructor tiện ích để chuyển từ Entity sang DTO
    public XeResponseDTO(Xe xe) {
        this.maXe = xe.getMaXe();
        this.bienSo = xe.getBienSo();
        this.hangXe = xe.getHangXe();       // ✅ chỉ lấy trực tiếp
        this.dongXe = xe.getDongXe();       // ✅ không gọi getTenDongXe()
        this.namSanXuat = xe.getNamSanXuat();
        this.mauSac = xe.getMauSac();
        this.trangThai = (xe.getTrangThai() == null || xe.getTrangThai().isBlank())
                ? "Hoạt động" : xe.getTrangThai();

        if (xe.getKhachHang() != null) {
            this.tenKhachHang = xe.getKhachHang().getTenKhachHang();
            this.maKhachHang = xe.getKhachHang().getMaKhachHang();
        } else {
            this.tenKhachHang = "Không xác định";
            this.maKhachHang = null;
        }
    }
}