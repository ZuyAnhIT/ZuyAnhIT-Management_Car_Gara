package com.example.gara_management.repository;

import com.example.gara_management.model.ChiTietPhieuSuaChua;
import com.example.gara_management.model.ChiTietPhieuSuaChuaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChiTietPhieuSuaChuaRepository extends JpaRepository<ChiTietPhieuSuaChua, ChiTietPhieuSuaChuaId> {
    
    List<ChiTietPhieuSuaChua> findByPhieuSuaChua_MaPhieu(Integer maPhieu);

    // [TÍNH NĂNG] Top 5 Dịch Vụ Sử Dụng Nhiều
    @org.springframework.data.jpa.repository.Query(
        "SELECT ct.dichVu.maDichVu, ct.dichVu.tenDichVu, SUM(ct.soLuong) AS sl " +
        "FROM ChiTietPhieuSuaChua ct " +
        "GROUP BY ct.dichVu.maDichVu, ct.dichVu.tenDichVu " +
        "ORDER BY sl DESC"
    )
    java.util.List<Object[]> findTopDichVuByUsage(org.springframework.data.domain.Pageable pageable);
}
