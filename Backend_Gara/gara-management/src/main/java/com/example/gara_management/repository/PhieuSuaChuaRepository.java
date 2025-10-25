package com.example.gara_management.repository;

import com.example.gara_management.model.PhieuSuaChua;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PhieuSuaChuaRepository extends JpaRepository<PhieuSuaChua, Integer>, JpaSpecificationExecutor<PhieuSuaChua> {
    
    List<PhieuSuaChua> findByTrangThai(String trangThai);
    
    List<PhieuSuaChua> findByXe_MaXe(Integer maXe);
    
    List<PhieuSuaChua> findByTho_MaTho(Integer maTho);
    
    @Query("SELECT p FROM PhieuSuaChua p " +
           "LEFT JOIN FETCH p.chiTietList ct " +
           "LEFT JOIN FETCH ct.dichVu " +
           "WHERE p.maPhieu = :maPhieu")
    PhieuSuaChua findByIdWithDetails(Integer maPhieu);
    Long countByTrangThai(String trangThai);
}

