package com.example.gara_management.repository;

import com.example.gara_management.model.DichVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DichVuRepository extends JpaRepository<DichVu, Integer>, JpaSpecificationExecutor<DichVu> {
    
    /**
     * Dùng để kiểm tra tên dịch vụ đã tồn tại chưa.
     */
    Optional<DichVu> findByTenDichVu(String tenDichVu);
    
    // Thêm JpaSpecificationExecutor nếu bạn muốn tái sử dụng logic tìm kiếm/phân trang
    // đã xây dựng cho LoaiDichVu.
}