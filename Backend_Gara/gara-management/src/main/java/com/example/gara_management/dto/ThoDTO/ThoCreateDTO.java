package com.example.gara_management.dto.ThoDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import jakarta.validation.constraints.*;
public class ThoCreateDTO {
    
    @NotBlank(message = "Tên Thợ không được để trống")
    @Size(max = 100)
    private String tenTho;

    @NotBlank(message = "Chuyên môn kkhông được để trống")
    private String chuyenMon;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "\\d{10-15}", message="Số điện thoại khoảng từ 10-15")
    private String soDienThoai;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    private int kinhNghiem;
}
