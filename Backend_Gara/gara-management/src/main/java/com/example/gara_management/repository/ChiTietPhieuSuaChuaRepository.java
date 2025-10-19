package com.example.gara_management.repository;

import com.example.gara_management.model.ChiTietPhieuSuaChua;
import com.example.gara_management.model.ChiTietPhieuSuaChuaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChiTietPhieuSuaChuaRepository extends JpaRepository<ChiTietPhieuSuaChua, ChiTietPhieuSuaChuaId> {
    
    List<ChiTietPhieuSuaChua> findByPhieuSuaChua_MaPhieu(Integer maPhieu);
}