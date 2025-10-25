package com.example.gara_management.repository;

import com.example.gara_management.model.LoaiDichVu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LoaiDichVuRepository extends JpaRepository<LoaiDichVu, Integer> {

    Optional<LoaiDichVu> findByTenLoai(String tenLoai);

    // ✅ Tổng số loại dịch vụ
    long count();

    // ✅ Số loại dịch vụ đang hoạt động
    long countByTrangThai(String trangThai);

    // ✅ Số loại dịch vụ mới thêm trong 30 ngày gần nhất
    @Query("SELECT COUNT(l) FROM LoaiDichVu l WHERE l.ngayTao >= :startDate")
    long countNewInLastMonth(LocalDateTime startDate);

    Page<LoaiDichVu> findAll(Specification<LoaiDichVu> spec, Pageable pageable);
}
