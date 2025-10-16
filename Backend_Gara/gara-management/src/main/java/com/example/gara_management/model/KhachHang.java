package com.example.gara_management.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "khach_hang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maKhachHang;

    @Column(nullable = false, length = 100)
    private String tenKhachHang;

    @Column(nullable = false, unique = true, length = 15)
    private String soDienThoai;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 200)
    private String diaChi;

    @Column(nullable = false, length = 50)
    private String loaiKhach; // Ví dụ: “Cá nhân”, “Doanh nghiệp”

    @Column(length = 200)
    private String ghiChu;

    @Column(nullable = false)
    private String trangThai = "Hoạt động";

    @Column(nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();
}
