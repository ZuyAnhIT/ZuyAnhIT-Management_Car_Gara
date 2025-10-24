package com.example.gara_management.dto.ThoDTO;


import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThoUpdateDTO {

    @NotBlank(message =  "Tên Thợ không được để trống" )
    private String tenTho;

    @NotBlank(message =  "Chuyên môn kkhông được để trống" )
    private String chuyenMon;

    @NotBlank(message =  "Số điện thoại không được để trống" )
    @Pattern(
        regexp = "^(0[0-9]{9}|\\+84\\s?[0-9]{9})$",
        message = "Số điện thoại VN không hợp lệ (vd: 0901234567, +84901234567, hoặc +84 901234567)"
    )
    private String soDienThoai;

    @NotBlank(message =  "Email không được để trống" )
    @Email(message =  "Email không hợp lệ" )
    private String email;

    @Min(value = 0, message = "Kinh nghiệm không được âm")
    @Max(value = 50, message = "Kinh nghiệm không được vượt quá 50 năm")
    private Integer kinhNghiem;


    private String trangThai;
}
