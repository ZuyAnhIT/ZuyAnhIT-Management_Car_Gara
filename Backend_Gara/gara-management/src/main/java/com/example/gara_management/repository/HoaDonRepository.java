package com.example.gara_management.repository;

import com.example.gara_management.model.HoaDon;
import com.example.gara_management.model.PhieuSuaChua;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer>, JpaSpecificationExecutor<HoaDon> {
    
    Optional<HoaDon> findByPhieuSuaChua_MaPhieu(Integer maPhieu);
    
    List<HoaDon> findByTrangThai(String trangThai);

    Long countByTrangThai(String trangThai);
}