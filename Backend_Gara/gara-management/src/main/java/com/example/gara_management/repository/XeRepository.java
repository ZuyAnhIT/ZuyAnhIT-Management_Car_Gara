package com.example.gara_management.repository;

import com.example.gara_management.model.Xe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface XeRepository extends JpaRepository<Xe, Integer>, JpaSpecificationExecutor<Xe> {

    // 🔍 Tìm xe theo biển số (duy nhất)
    Optional<Xe> findByBienSo(String bienSo);

    // 🔍 Tìm danh sách xe theo hãng xe
    List<Xe> findByHangXe(String hangXe);

    // 🔍 Tìm danh sách xe theo khách hàng (FK)
    List<Xe> findByKhachHang_MaKhachHang(Integer maKhachHang);

    // 🔍 Tìm xe theo trạng thái (Hoạt động / Đã xóa / Bảo trì…)
    List<Xe> findByTrangThai(String trangThai);
    // ✅ Đếm tổng số xe (JPA có sẵn count())
    long count();

    // ✅ Đếm xe đang hoạt động
    long countByTrangThai(String trangThai);
}
