package com.example.gara_management.model;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "HoaDon")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaHoaDon")
    private Integer maHoaDon;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaPhieu", nullable = false, unique = true)
    private PhieuSuaChua phieuSuaChua;
    
    @Column(name = "NgayLapHoaDon", nullable = false)
    private LocalDateTime ngayLapHoaDon;
    
    @Column(name = "ThoiGianThanhCong")
    private LocalDateTime thoiGianThanhCong;
    
    @Column(name = "KieuThanhToan", length = 50, nullable = false)
    private String kieuThanhToan = "Tiền mặt";
    
    @Column(name = "TrangThai", length = 50, nullable = false)
    private String trangThai = "Chưa thanh toán";
    
    @Column(name = "TongTien", precision = 18, scale = 2)
    private BigDecimal tongTien;
    
    @PrePersist
    protected void onCreate() {
        if (ngayLapHoaDon == null) {
            ngayLapHoaDon = LocalDateTime.now();
        }
        if (kieuThanhToan == null) {
            kieuThanhToan = "Tiền mặt";
        }
        if (trangThai == null) {
            trangThai = "Chưa thanh toán";
        }
    }
    
    // Method để đánh dấu đã thanh toán
    public void markAsPaid() {
        this.trangThai = "Đã thanh toán";
        this.thoiGianThanhCong = LocalDateTime.now();
    }
}