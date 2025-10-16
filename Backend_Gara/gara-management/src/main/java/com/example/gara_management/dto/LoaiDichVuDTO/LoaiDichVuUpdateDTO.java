package com.example.gara_management.dto.LoaiDichVuDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Size;

@Data // Getters, Setters, etc.
@NoArgsConstructor
@AllArgsConstructor
public class LoaiDichVuUpdateDTO {

    // Không dùng @NotBlank nữa, cho phép null hoặc rỗng
    @Size(max = 100, message = "Tên loại dịch vụ không được vượt quá 100 ký tự.")
    private String tenLoai; // Có thể là null

    // Không dùng @NotBlank hoặc @Pattern, logic kiểm tra chuyển sang Service
    private String trangThai; // Có thể là null
    
}