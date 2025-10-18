package com.example.gara_management.dto.XeDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class XeUpdateDTO {

    @NotBlank(message = "Biển số xe không được để trống.")
    @Size(max = 20, message = "Biển số xe không được vượt quá 20 ký tự.")
    private String bienSo;

    @Size(max = 50, message = "Hãng xe không được vượt quá 50 ký tự.")
    private String hangXe;

    @Size(max = 50, message = "Dòng xe không được vượt quá 50 ký tự.")
    private String dongXe;

    @Min(value = 1886, message = "Năm sản xuất phải lớn hơn hoặc bằng 1886.") // 1886: năm chiếc xe đầu tiên ra đời
    private Integer namSanXuat;

    @Size(max = 30, message = "Màu sắc không được vượt quá 30 ký tự.")
    private String mauSac;

    @Size(max = 30, message = "Trạng thái không được vượt quá 30 ký tự.")
    private String trangThai;

    //  Liên kết với khách hàng (nếu muốn cập nhật chủ xe)
    private Integer maKhachHang;

}
