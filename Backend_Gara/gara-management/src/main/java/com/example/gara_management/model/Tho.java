package com.example.gara_management.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Tho")
@Data // Cung cấp Getters, Setters, toString, equals/hashCode
@NoArgsConstructor // Constructor không đối số
@AllArgsConstructor // Constructor đầy đủ đối số
public class Tho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    @Column(name = "MaTho")
    private Integer maTho;

    @Column(name = "TenTho", length = 100, nullable = false)
    private String tenTho;

    @Column(name = "ChuyenMon", length = 100, nullable = false)
    private String chuyenMon;

    @Column(name = "SoDienThoai", length = 15, nullable = false, unique = true)
    private String soDienThoai;

    @Column(name = "Email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "TrangThai", length = 50, nullable = false)
    private String trangThai = "Hoạt động"; // Giá trị mặc định

    @Column(name = "KinhNghiem")
    private Integer kinhNghiem; // Ràng buộc CHECK (>= 0) sẽ được xử lý ở tầng Service/DTO

    @Column(name = "NgayVaoLam", nullable = false)
    private LocalDateTime ngayVaoLam = LocalDateTime.now();

    // Constructor tiện ích cho việc tạo mới
    public Tho(String tenTho, String chuyenMon, String soDienThoai, String email, Integer kinhNghiem) {
        this.tenTho = tenTho;
        this.chuyenMon = chuyenMon;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.kinhNghiem = kinhNghiem;
        this.trangThai = "Hoạt động";
        this.ngayVaoLam = LocalDateTime.now();
    }
}