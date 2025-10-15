package com.example.gara_management.repository;

import com.example.gara_management.model.LoaiDichVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
// Khóa chính là Integer (theo Model đã sửa)
public interface LoaiDichVuRepository extends JpaRepository<LoaiDichVu, Integer> { 

   
    Optional<LoaiDichVu> findByTenLoai(String tenLoai);
    
}