package com.example.gara_management.dto.XeDTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class XeCreateDTO {

    @NotBlank(message = "Biển số xe không được để trống.")
    @Size(max = 15, message = "Biển số xe không được vượt quá 15 ký tự.")
    private String bienSo;

    @NotBlank(message = "Hãng xe không được để trống.")
    @Size(max = 50, message = "Hãng xe không được vượt quá 50 ký tự.")
    private String hangXe;

    @NotBlank(message = "Dòng xe không được để trống.")
    @Size(max = 15, message = "Dòng xe không được vượt quá 15 ký tự.")
    private String dongXe;

    @NotNull(message = "Năm sản xuất không được để trống.")
    @Min(value = 2000, message = "Năm sản xuất phải lớn hơn hoặc bằng 2000.")
    private Integer namSanXuat;

    @NotBlank(message = "Màu sắc không được để trống.")
    @Size(max = 15, message = "Màu sắc không được vượt quá 15 ký tự.")
    private String mauSac;

    // Có thể null, chỉ dùng khi muốn gán xe cho khách hàng
    private Integer maKhachHang;
}
