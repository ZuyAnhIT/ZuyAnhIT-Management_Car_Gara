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
    @Pattern(regexp = "^(\\+84|84|0)\\s?(3|5|7|8|9)\\d{1,2}\\s?\\d{3}\\s?\\d{3}$", 
            message = "Số điện thoại Việt Nam không hợp lệ")
    private String soDienThoai;

    @NotBlank(message =  "Email không được để trống" )
    @Email(message =  "Email không hợp lệ" )
    private String email;

    @Min(value = 0, message = "Kinh nghiệm không được âm")
    @Max(value = 50, message = "Kinh nghiệm không được vượt quá 50 năm")
    private Integer kinhNghiem;


    private String trangThai;
}
