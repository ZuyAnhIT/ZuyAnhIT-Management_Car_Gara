package com.example.gara_management.dto.KhachHangDTO;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangUpdateDTO {
    @Size(min = 2, max = 50, message = "Tên khách hàng phải từ 2–50 ký tự")
    @Pattern(regexp = "^[\\p{L}\\s'.-]+$", message = "Tên khách hàng chỉ được chứa chữ cái và khoảng trắng")
    private String tenKhachHang; // Cho phép null (partial update)

    @Pattern(regexp = "^(0|\\+84)(3|5|7|8|9)\\d{8}$", message = "Số điện thoại Việt Nam không hợp lệ")
    private String soDienThoai; // Có thể null

    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email không được dài quá 100 ký tự")
    private String email; // Có thể null

    @Size(min = 5, max = 255, message = "Địa chỉ phải từ 5–255 ký tự")
    private String diaChi; // Có thể null

    @Pattern(regexp = "^(Cá nhân|Doanh nghiệp)$", message = "Loại khách hàng chỉ có thể là 'Cá nhân' hoặc 'Doanh nghiệp'")
    private String loaiKhach; // Có thể null

    @Size(max = 500, message = "Ghi chú không được dài quá 500 ký tự")
    private String ghiChu; // Có thể null

    @Pattern(regexp = "^(Hoạt động|Đã xóa)$", message = "Trạng thái chỉ có thể là 'Hoạt động' hoặc 'Đã xóa'")
    private String trangThai; // Có thể null
}
