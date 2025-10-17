package com.example.gara_management.dto.KhachHangDTO;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor // Quan trọng cho DTO
@AllArgsConstructor // Quan trọng cho DTO
public class KhachHangUpdateDTO {
    
    // Tên Khách Hàng: NVARCHAR(100)
    @Size(min = 2, max = 100, message = "Tên khách hàng phải từ 2–100 ký tự.") 
    @Pattern(regexp = "^[\\p{L}\\s'.-]+$", message = "Tên khách hàng chỉ được chứa chữ cái, khoảng trắng và các ký tự đặc biệt '.-")
    private String tenKhachHang; 

    // Số Điện Thoại: VARCHAR(15)
    @Size(max = 15, message = "Số điện thoại không được dài quá 15 ký tự.")
    @Pattern(regexp = "^(0|\\+84)(3|5|7|8|9)\\d{8}$", message = "Số điện thoại Việt Nam không hợp lệ.")
    private String soDienThoai; 

    // Email: VARCHAR(50)
    @Email(message = "Email không hợp lệ.")
    @Size(max = 50, message = "Email không được dài quá 50 ký tự.") // Chỉnh sửa: 100 -> 50
    private String email; 

    // Địa chỉ: NVARCHAR(200)
    @Size(min = 5, max = 200, message = "Địa chỉ phải từ 5–200 ký tự.") // Chỉnh sửa: 255 -> 200
    private String diaChi; 

    // Loại Khách: NVARCHAR(50)
    @Pattern(regexp = "^(Cá nhân|Doanh nghiệp)$", message = "Loại khách hàng chỉ có thể là 'Cá nhân' hoặc 'Doanh nghiệp'.")
    private String loaiKhach; 

    // Ghi Chú: NVARCHAR(200)
    @Size(max = 200, message = "Ghi chú không được dài quá 200 ký tự.") // Chỉnh sửa: 500 -> 200
    private String ghiChu; 

    // Trạng Thái: NVARCHAR(50)
    @Pattern(regexp = "^(Hoạt động|Đã xóa)$", message = "Trạng thái chỉ có thể là 'Hoạt động' hoặc 'Đã xóa'.")
    private String trangThai; 
}