package com.example.gara_management.repository;

import com.example.gara_management.model.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer>, JpaSpecificationExecutor<KhachHang> {

    // Kiểm tra trùng số điện thoại
    Optional<KhachHang> findBySoDienThoai(String soDienThoai);

    // Kiểm tra trùng email
    Optional<KhachHang> findByEmail(String email);

    //  Hàm đếm tổng số khách hàng trong hệ thống
    long count();

    //  Đếm khách hàng theo loại ("Cá nhân" hoặc "Doanh nghiệp")
    long countByLoaiKhach(String loaiKhach);
}
