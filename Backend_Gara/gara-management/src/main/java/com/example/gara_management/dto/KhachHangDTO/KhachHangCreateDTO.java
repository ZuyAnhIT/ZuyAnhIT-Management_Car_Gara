package com.example.gara_management.dto.KhachHangDTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor; // Thường nên có
import lombok.AllArgsConstructor; // Thường nên có

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangCreateDTO {
    
    // --- 1. TenKhachHang ---
    @NotBlank(message = "Tên khách hàng không được để trống.")
    @Size(min = 2, max = 100, message = "Tên khách hàng phải từ 2–100 ký tự.") // Tối đa 100 ký tự (theo DB)
    @Pattern(regexp = "^[\\p{L}\\s'.-]+$", message = "Tên khách hàng chỉ được chứa chữ cái, khoảng trắng và các ký tự đặc biệt '.-")
    private String tenKhachHang;

    // --- 2. SoDienThoai ---
    // Giữ max 15 ký tự (theo VARCHAR(15) trong DB)
    @NotBlank(message = "Số điện thoại không được để trống.")
    @Size(max = 15, message = "Số điện thoại không được dài quá 15 ký tự.")
    @Pattern(regexp = "^(\\+84|84|0)\\s?(3|5|7|8|9)\\d{1,2}\\s?\\d{3}\\s?\\d{3}$", 
            message = "Số điện thoại Việt Nam không hợp lệ")
    private String soDienThoai;

    // --- 3. Email ---
    // Giữ max 50 ký tự (theo VARCHAR(50) trong DB)
    @NotBlank(message = "Email không được để trống.")
    @Email(message = "Email không hợp lệ.")
    @Size(max = 50, message = "Email không được dài quá 50 ký tự.") 
    private String email;

    // --- 4. DiaChi ---
    // Tối đa 200 ký tự (theo NVARCHAR(200) trong DB)
    @NotBlank(message = "Địa chỉ không được để trống.")
    @Size(min = 5, max = 200, message = "Địa chỉ phải từ 5–200 ký tự.")
    private String diaChi;

    // --- 5. LoaiKhach ---
    @NotBlank(message = "Loại khách hàng không được để trống.")
    @Pattern(regexp = "^(Cá nhân|Doanh nghiệp)$", message = "Loại khách hàng chỉ có thể là 'Cá nhân' hoặc 'Doanh nghiệp'.")
    private String loaiKhach;

    // --- 6. GhiChu ---
    // Tối đa 200 ký tự (theo NVARCHAR(200) trong DB)
    @Size(max = 200, message = "Ghi chú không được dài quá 200 ký tự.") 
    private String ghiChu; // Có thể null
}
