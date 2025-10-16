package com.example.gara_management.service;

import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.KhachHangDTO.KhachHangCreateDTO;
import com.example.gara_management.dto.KhachHangDTO.KhachHangResponseDTO;
import com.example.gara_management.dto.KhachHangDTO.KhachHangUpdateDTO;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;
import com.example.gara_management.model.KhachHang;
import com.example.gara_management.repository.KhachHangRepository;
import com.example.gara_management.util.JpaSpecificationUtil;
import com.example.gara_management.util.SortUtils;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@Service
public class KhachHangService {

    private final KhachHangRepository khachHangRepository;
    private static final String[] VALID_TRANG_THAI = {"Hoạt động", "Đã xóa"};

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

        return khachHangRepository.save(newKH);
    }

    // 🧠 READ - LẤY DANH SÁCH CÓ PHÂN TRANG & SẮP XẾP
    public PageResponseDTO<KhachHangResponseDTO> getAllCustomers(
            int page, int size, String sortBy, String sortDirection) {

        // Giới hạn 10 khách/trang
        size = Math.min(size, 10);
        page = Math.max(0, page);

        String safeSortBy = mapSortableField(sortBy);
        Sort sort = SortUtils.createSort(safeSortBy, sortDirection, "maKhachHang", Sort.Direction.DESC);

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<KhachHang> resultPage = khachHangRepository.findAll(pageable);

        return new PageResponseDTO<>(resultPage.map(KhachHangResponseDTO::new));
    }

    // 🧠 SEARCH - TÌM KIẾM + PHÂN TRANG + SẮP XẾP
    public PageResponseDTO<KhachHangResponseDTO> searchCustomers(
            int page, int size, String sortBy, String sortDirection,
            String tenKhachHang, String soDienThoai, String loaiKhach) {

        Specification<KhachHang> spec = Specification.<KhachHang>where(null)
                .and(JpaSpecificationUtil.attributeContains("tenKhachHang", tenKhachHang))
                .and(JpaSpecificationUtil.attributeContains("soDienThoai", soDienThoai))
                .and(JpaSpecificationUtil.attributeEquals("loaiKhach", loaiKhach));

        // Giới hạn 10 khách/trang
        size = Math.min(size, 10);
        page = Math.max(0, page);

        String safeSortBy = mapSortableField(sortBy);
        Sort sort = SortUtils.createSort(safeSortBy, sortDirection, "maKhachHang", Sort.Direction.DESC);

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<KhachHang> pageResult = khachHangRepository.findAll(spec, pageable);

        return new PageResponseDTO<>(pageResult.map(KhachHangResponseDTO::new));
    }

    // 🧠 UPDATE
    @Transactional
    public KhachHang updateCustomer(Integer id, KhachHangUpdateDTO dto) {
        KhachHang existing = khachHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với mã: " + id));

        Optional.ofNullable(dto.getTenKhachHang()).filter(t -> !t.isBlank()).ifPresent(existing::setTenKhachHang);

        Optional.ofNullable(dto.getSoDienThoai()).filter(s -> !s.isBlank()).ifPresent(sdt -> {
            khachHangRepository.findBySoDienThoai(sdt).ifPresent(kh -> {
                if (!kh.getMaKhachHang().equals(id))
                    throw new ResourceAlreadyExistsException("Số điện thoại đã tồn tại: " + sdt);
            });
            existing.setSoDienThoai(sdt);
        });

        Optional.ofNullable(dto.getEmail()).filter(e -> !e.isBlank()).ifPresent(email -> {
            khachHangRepository.findByEmail(email).ifPresent(kh -> {
                if (!kh.getMaKhachHang().equals(id))
                    throw new ResourceAlreadyExistsException("Email đã tồn tại: " + email);
            });
            existing.setEmail(email);
        });

        Optional.ofNullable(dto.getDiaChi()).filter(d -> !d.isBlank()).ifPresent(existing::setDiaChi);
        Optional.ofNullable(dto.getLoaiKhach()).filter(l -> !l.isBlank()).ifPresent(existing::setLoaiKhach);
        Optional.ofNullable(dto.getGhiChu()).ifPresent(existing::setGhiChu);

        if (dto.getTrangThai() != null && !dto.getTrangThai().isBlank()) {
            if (!Arrays.asList(VALID_TRANG_THAI).contains(dto.getTrangThai())) {
                throw new IllegalArgumentException("Trạng thái không hợp lệ. Chỉ chấp nhận: " + String.join(", ", VALID_TRANG_THAI));
            }
            existing.setTrangThai(dto.getTrangThai());
        }

        return khachHangRepository.save(existing);
    }

    // 🧠 SOFT DELETE
    @Transactional
    public KhachHang softDeleteCustomer(Integer id) {
        KhachHang entity = khachHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với mã: " + id));

        if ("Đã xóa".equals(entity.getTrangThai())) {
            throw new IllegalStateException("Khách hàng này đã bị xóa trước đó.");
        }

        entity.setTrangThai("Đã xóa");
        return khachHangRepository.save(entity);
    }
}
