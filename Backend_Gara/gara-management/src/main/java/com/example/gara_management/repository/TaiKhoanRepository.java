package com.example.gara_management.repository;

import com.example.gara_management.model.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {
    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);
    Optional<TaiKhoan> findByEmail(String email);
    boolean existsByTenDangNhap(String tenDangNhap);
    boolean existsByEmail(String email);

    // =================== HÀM ĐẾM ===================
    long count(); // ✅ Đếm tổng số tài khoản (mã tài khoản)

    long countByVaiTro(String vaiTro); // ✅ Đếm theo vai trò, ví dụ: "NHANVIEN", "KHACHHANG"

    long countByTrangThai(String trangThai); // ✅ Đếm theo trạng thái, ví dụ: "Hoạt động", "Ngừng hoạt động"
}
