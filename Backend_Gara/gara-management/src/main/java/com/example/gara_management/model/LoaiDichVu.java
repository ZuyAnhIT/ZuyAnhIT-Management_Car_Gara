package com.example.gara_management.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LoaiDichVu")
@Data // Cung cấp Getters, Setters, toString, equals/hashCode
@NoArgsConstructor // Cung cấp constructor không đối số
@AllArgsConstructor // Cung cấp constructor với tất cả đối số
public class LoaiDichVu {

    @Id // Khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT trong SQL
    @Column(name = "MaLoai")
    private Integer maLoai; // Kiểu Integer cho AUTO_INCREMENT

    @Column(name = "TenLoai", length = 100, nullable = false, unique = true)
    private String tenLoai;

    // Trường mới: TrangThai
    @Column(name = "TrangThai", length = 50, nullable = false)
    private String trangThai = "Hoạt động"; // Giá trị mặc định

    // columnDefinition dùng để định nghĩa kiểu dữ liệu và giá trị mặc định trong SQL
    // nhưng ta vẫn nên để logic đặt giá trị mặc định trong Constructor/Setter.
    @Column(name = "NgayTao", nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();


    // Constructor dùng để tạo mới (không cần MaLoai và NgayTao/TrangThai nếu dùng giá trị mặc định)
    public LoaiDichVu(String tenLoai) {
        this.tenLoai = tenLoai;
        this.trangThai = "Hoạt động"; // Đặt giá trị mặc định rõ ràng
        this.ngayTao = LocalDateTime.now(); // Đặt giá trị mặc định rõ ràng
    }
}