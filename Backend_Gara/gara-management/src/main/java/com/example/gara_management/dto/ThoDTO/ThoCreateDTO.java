package com.example.gara_management.dto.ThoDTO;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThoCreateDTO {
    
    @NotBlank(message = "Tên thợ không được để trống")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Chuyên môn không được để trống")
    private String specialty;

    @NotBlank(message = "Số Điện Thoại không được để trống")
    @Pattern(regexp = "\\d{10,15}", message = "Số điện thoại khoảng 10-15")
    private String number;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    
    private Integer experience;
}
