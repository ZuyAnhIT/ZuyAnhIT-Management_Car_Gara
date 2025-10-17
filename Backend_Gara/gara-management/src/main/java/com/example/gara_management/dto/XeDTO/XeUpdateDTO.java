package com.example.gara_management.dto.XeDTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO dùng cho việc cập nhật thông tin Xe trong hệ thống Gara.
 * - Áp dụng validation chặt chẽ tương tự như KhachHangCreateDTO.
 * - Dữ liệu đầu vào phải hợp lệ trước khi gửi xuống Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XeUpdateDTO {

    // --- 1️⃣ Biển số xe ---
    // Dù không thay đổi nhiều, vẫn nên cho phép update (và đảm bảo unique trong Service)
    @NotBlank(message = "Biển số xe không được để trống.")
    @Size(max = 15, message = "Biển số xe không được vượt quá 15 ký tự.")
    @Pattern(regexp = "^[A-Z0-9\\-]+$", message = "Biển số xe chỉ được chứa chữ in hoa, số và dấu gạch ngang.")
    private String bienSo;

    // --- 2️⃣ Hãng xe ---
    @NotBlank(message = "Hãng xe không được để trống.")
    @Size(max = 50, message = "Hãng xe không được vượt quá 50 ký tự.")
    private String hangXe;

    // --- 3️⃣ Dòng xe ---
    @NotBlank(message = "Dòng xe không được để trống.")
    @Size(max = 15, message = "Dòng xe không được vượt quá 15 ký tự.")
    private String dongXe;

    // --- 4️⃣ Năm sản xuất ---
    @NotNull(message = "Năm sản xuất không được để trống.")
    @Min(value = 2000, message = "Năm sản xuất phải lớn hơn hoặc bằng 2000.")
    @Max(value = 2100, message = "Năm sản xuất không hợp lệ.")
    private Integer namSanXuat;

    // --- 5️⃣ Màu sắc ---
    @NotBlank(message = "Màu sắc không được để trống.")
    @Size(max = 15, message = "Màu sắc không được vượt quá 15 ký tự.")
    private String mauSac;

    // --- 6️⃣ Trạng thái ---
    // Cho phép các giá trị như: "Hoạt động", "Đang bảo trì", "Đã xóa"
    @NotBlank(message = "Trạng thái không được để trống.")
    @Pattern(regexp = "^(Hoạt động|Đang bảo trì|Đã xóa)$",
            message = "Trạng thái chỉ có thể là 'Hoạt động', 'Đang bảo trì' hoặc 'Đã xóa'.")
    private String trangThai;

    // --- 7️⃣ Khách hàng ---
    @NotNull(message = "Mã khách hàng không được để trống.")
    private Integer maKhachHang;
}
