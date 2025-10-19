package com.example.gara_management.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietPhieuSuaChua")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(ChiTietPhieuSuaChuaId.class)
public class ChiTietPhieuSuaChua {
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaPhieu", nullable = false)
    private PhieuSuaChua phieuSuaChua;
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDichVu", nullable = false)
    private DichVu dichVu;
    
    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong = 1;
    
    @Column(name = "DonGia", precision = 18, scale = 2, nullable = false)
    private BigDecimal donGia;
    
    // ThanhTien sẽ được tính tự động bởi database
    @Column(name = "ThanhTien", precision = 18, scale = 2, insertable = false, updatable = false)
    private BigDecimal thanhTien;
    
    // Method để tính thành tiền trong Java nếu cần
    public BigDecimal calculateThanhTien() {
        if (soLuong != null && donGia != null) {
            return donGia.multiply(BigDecimal.valueOf(soLuong));
        }
        return BigDecimal.ZERO;
    }
}