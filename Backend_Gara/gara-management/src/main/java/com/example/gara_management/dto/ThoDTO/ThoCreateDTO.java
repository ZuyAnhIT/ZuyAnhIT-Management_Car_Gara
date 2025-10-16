package com.example.gara_management.dto.ThoDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThoCreateDTO {
    
    @NotBlank(message = "Tên Thợ không được để trống")
    @Size(max = 100)
    private String tenTho;

    @NotBlank(message = "Chuyên môn kkhông được để trống")
    private String chuyenMon;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "\\d{10,15}", message="Số điện thoại phải từ 10 đến 15 chữ số")
    @Schema(example = "0912345678", description = "Số điện thoại gồm 10-15 chữ số")
    private String soDienThoai;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    private Integer kinhNghiem;
}
