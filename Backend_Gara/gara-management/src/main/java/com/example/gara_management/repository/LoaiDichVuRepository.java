package com.example.gara_management.repository;

import com.example.gara_management.model.LoaiDichVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// Khóa chính là Integer (theo Model đã sửa)
public interface LoaiDichVuRepository extends JpaRepository<LoaiDichVu, Integer> { 

    /**
     * Tự động được triển khai bởi Spring Data JPA.
     * Dùng để kiểm tra xem TenLoai đã tồn tại trong DB chưa.
     */
    Optional<LoaiDichVu> findByTenLoai(String tenLoai);
    
    // Nếu dùng AUTO_INCREMENT (Integer), không cần các phương thức tìm MaxMaLoai thủ công.
    // Việc này sẽ do DB/JPA xử lý.
}