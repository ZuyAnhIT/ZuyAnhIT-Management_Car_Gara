package com.example.gara_management.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "DichVu")
@Data // Cung cấp Getters, Setters, toString, equals/hashCode
@NoArgsConstructor // Constructor không đối số
@AllArgsConstructor // Constructor đầy đủ đối số
@Builder
public class DichVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    @Column(name = "MaDichVu")
    private Integer maDichVu;

    @Column(name = "TenDichVu", length = 100, nullable = false)
    private String tenDichVu;

    @Column(name = "MoTa", length = 255)
    private String moTa;

    // Kiểu TEXT trong SQL thường được ánh xạ thành String trong Java
    @Column(name = "AnhDichVu", columnDefinition = "TEXT")
    private String anhDichVu;

    @Column(name = "SoLuongTon", nullable = false)
    private Integer soLuongTon;

    @Column(name = "SoLuongBan", nullable = false)
    private Integer soLuongBan;

    // DECIMAL(18,2) -> BigDecimal
    @Column(name = "Gia", precision = 18, scale = 2, nullable = false)
    private BigDecimal gia;

    @Column(name = "ThoiGianUocTinh")
    private Integer thoiGianUocTinh;

    @Column(name = "TrangThai", length = 50, nullable = false)
    private String trangThai = "Còn hàng"; // Giá trị mặc định

    @Column(name = "NgayTao", nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();

    // --- Mối quan hệ Khóa ngoại (Foreign Key) ---
    // Nhiều Dịch vụ thuộc về một Loại Dịch vụ (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY) // LAZY loading là tốt cho hiệu năng
    @JoinColumn(name = "MaLoai", nullable = false) // Tên cột khóa ngoại trong bảng DichVu
    @JsonIgnore
    private LoaiDichVu loaiDichVu;

    // --- Constructor tiện ích cho việc tạo mới ---
    public DichVu(String tenDichVu, String moTa, String anhDichVu, Integer soLuongTon, Integer soLuongBan, BigDecimal gia, Integer thoiGianUocTinh, LoaiDichVu loaiDichVu) {
        this.tenDichVu = tenDichVu;
        this.moTa = moTa;
        this.anhDichVu = anhDichVu;
        this.soLuongTon = soLuongTon;
        this.soLuongBan = soLuongBan;
        this.gia = gia;
        this.thoiGianUocTinh = thoiGianUocTinh;
        this.loaiDichVu = loaiDichVu;
        this.trangThai = "Còn hàng";
        this.ngayTao = LocalDateTime.now();
    }

    public static Object builder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'builder'");
    }
}