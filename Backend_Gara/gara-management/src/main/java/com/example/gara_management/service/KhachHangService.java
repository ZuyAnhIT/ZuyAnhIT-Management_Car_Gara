package com.example.gara_management.service;

import com.example.gara_management.dto.KhachHangDTO.KhachHangCreateDTO;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.model.KhachHang;
import com.example.gara_management.repository.KhachHangRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KhachHangService {

    private final KhachHangRepository khachHangRepository;

    public KhachHangService(KhachHangRepository khachHangRepository) {
        this.khachHangRepository = khachHangRepository;
    }

    /**
     * 🧠 Thêm mới khách hàng
     * - Kiểm tra trùng số điện thoại và email
     * - Lưu dữ liệu mới vào DB
     */
    @Transactional
    public KhachHang themKhachHang(KhachHangCreateDTO dto) {

        // 🔍 Kiểm tra trùng số điện thoại
        khachHangRepository.findBySoDienThoai(dto.getSoDienThoai()).ifPresent(kh -> {
            throw new ResourceAlreadyExistsException(" So dien thoai ton tai: " + dto.getSoDienThoai());
        });

        // 🔍 Kiểm tra trùng email
        khachHangRepository.findByEmail(dto.getEmail()).ifPresent(kh -> {
            throw new ResourceAlreadyExistsException(" Email da ton tai: " + dto.getEmail());
        });

        // 🧱 Chuyển DTO → Entity
        KhachHang newKH = new KhachHang();
        newKH.setTenKhachHang(dto.getTenKhachHang());
        newKH.setSoDienThoai(dto.getSoDienThoai());
        newKH.setEmail(dto.getEmail());
        newKH.setDiaChi(dto.getDiaChi());
        newKH.setLoaiKhach(dto.getLoaiKhach());
        newKH.setGhiChu(dto.getGhiChu());
        newKH.setTrangThai("Hoạt động");

        // 💾 Lưu vào DB
        return khachHangRepository.save(newKH);
    }
}
