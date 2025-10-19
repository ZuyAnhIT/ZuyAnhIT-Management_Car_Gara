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

    // ĐÃ XÓA @NotBlank: Cho phép null hoặc rỗng để cập nhật một phần
    @Size(max = 20, message = "Biển số xe không được vượt quá 20 ký tự.")
    private String bienSo; // Có thể null

    @Size(max = 50, message = "Hãng xe không được vượt quá 50 ký tự.")
    private String hangXe; // Có thể null

    @Size(max = 50, message = "Dòng xe không được vượt quá 50 ký tự.")
    private String dongXe; // Có thể null

    @Min(value = 1886, message = "Năm sản xuất phải lớn hơn hoặc bằng 1886.")
    private Integer namSanXuat; // Có thể null

    @Size(max = 30, message = "Màu sắc không được vượt quá 30 ký tự.")
    private String mauSac; // Có thể null

    // Trạng thái (không cần @Pattern nếu bạn kiểm tra trong service)
    @Size(max = 30, message = "Trạng thái không được vượt quá 30 ký tự.")
    private String trangThai; // Có thể null

    private Integer maKhachHang; // Có thể null
}

