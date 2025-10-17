package com.example.gara_management.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Xe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Xe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    @Column(name = "MaXe")
    private Integer maXe;

    @Column(name = "BienSo", length = 15, nullable = false, unique = true)
    private String bienSo;

    @Column(name = "HangXe", length = 50, nullable = false)
    private String hangXe;

    @Column(name = "DongXe", length = 15, nullable = false)
    private String dongXe;

    @Column(name = "NamSanXuat", nullable = false)
    private Integer namSanXuat;

    @Column(name = "MauSac", length = 15, nullable = false)
    private String mauSac;

    @Column(name = "TrangThai", length = 50, nullable = false)
    private String trangThai = "Hoạt động"; // Mặc định

    @Column(name = "NgayTao", nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();

    // --- Mối quan hệ khóa ngoại ---
    // Mỗi Xe thuộc về một Khách Hàng (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhachHang") // Khóa ngoại trỏ tới bảng KhachHang
    @JsonIgnore
    private KhachHang khachHang;

    // --- Constructor tiện ích cho việc tạo mới ---
    public Xe(String bienSo, String hangXe, String dongXe, Integer namSanXuat, String mauSac, KhachHang khachHang) {
        this.bienSo = bienSo;
        this.hangXe = hangXe;
        this.dongXe = dongXe;
        this.namSanXuat = namSanXuat;
        this.mauSac = mauSac;
        this.khachHang = khachHang;
        this.trangThai = "Hoạt động";
        this.ngayTao = LocalDateTime.now();
    }
}
