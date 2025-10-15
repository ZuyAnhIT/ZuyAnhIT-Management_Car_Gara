package com.example.gara_management.dto.LoaiDichVuDTO;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data // Tự động tạo Getters, Setters, toString, equals/hashCode
@NoArgsConstructor // Constructor không đối số
@AllArgsConstructor // Constructor đầy đủ đối số
public class LoaiDichVuCreateDTO {

    @NotBlank(message = "Tên loại dịch vụ không được để trống.")
    @Size(max = 100, message = "Tên loại dịch vụ không được vượt quá 100 ký tự.")
    private String tenLoai;

    // Không cần viết getters/setters/constructors thủ công nhờ Lombok @Data, @NoArgsConstructor, @AllArgsConstructor.
}