package com.example.gara_management.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "PhieuSuaChua")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuSuaChua {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaPhieu")
    private Integer maPhieu;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaXe", nullable = false)
    private Xe xe;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTho", nullable = false)
    private Tho tho;
    
    @Column(name = "NgayLap", nullable = false)
    private LocalDateTime ngayLap;
    
    @Column(name = "MoTa", length = 255)
    private String moTa;
    
    @Column(name = "TrangThai", length = 50, nullable = false)
    private String trangThai = "Chờ xử lý";
    
    @Column(name = "TongTien", precision = 18, scale = 2)
    private BigDecimal tongTien;
    
    @OneToMany(mappedBy = "phieuSuaChua", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietPhieuSuaChua> chiTietList;
    
    @OneToOne(mappedBy = "phieuSuaChua", cascade = CascadeType.ALL)
    private HoaDon hoaDon;
    
    @PrePersist
    protected void onCreate() {
        if (ngayLap == null) {
            ngayLap = LocalDateTime.now();
        }
        if (trangThai == null) {
            trangThai = "Chờ xử lý";
        }
    }
}