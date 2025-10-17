package com.example.gara_management.model;

import jakarta.persistence.*;
import lombok.*;

// Chú thích Lombok giúp bạn không cần viết thủ công getters/setters/constructors
@Entity
@Table(name = "KhachHang")
@Data // Cung cấp Getters, Setters, toString, equals/hashCode
@NoArgsConstructor // Constructor không đối số
@AllArgsConstructor // Constructor đầy đủ đối số
public class KhachHang {

    @Id // Khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    @Column(name = "MaKhachHang")
    private Integer maKhachHang;

    @Column(name = "TenKhachHang", length = 100, nullable = false)
    private String tenKhachHang;

    @Column(name = "SoDienThoai", length = 15, nullable = false, unique = true)
    private String soDienThoai;

    @Column(name = "Email", length = 50, nullable = false)
    private String email;

    @Column(name = "TrangThai", length = 50, nullable = false)
    private String trangThai = "Hoạt động"; // Giá trị mặc định

    @Column(name = "DiaChi", length = 200, nullable = false)
    private String diaChi;

    @Column(name = "LoaiKhach", length = 50, nullable = false)
    private String loaiKhach;

    @Column(name = "GhiChu", length = 200)
    private String ghiChu; // Cho phép NULL

   
}