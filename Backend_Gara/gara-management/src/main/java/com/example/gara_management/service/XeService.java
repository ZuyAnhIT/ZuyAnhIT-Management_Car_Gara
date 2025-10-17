package com.example.gara_management.service;

import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.XeDTO.XeResponseDTO;
import com.example.gara_management.dto.XeDTO.XeUpdateDTO;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;
import com.example.gara_management.model.KhachHang;
import com.example.gara_management.model.Xe;
import com.example.gara_management.repository.KhachHangRepository;
import com.example.gara_management.repository.XeRepository;
import com.example.gara_management.util.JpaSpecificationUtil;
import com.example.gara_management.util.SortUtils;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class XeService {

    private final XeRepository xeRepository;
    private final KhachHangRepository khachHangRepository;
    public XeService(XeRepository xeRepository, KhachHangRepository khachHangRepository) {
        this.xeRepository = xeRepository;
        this.khachHangRepository = khachHangRepository;
    }

    // ================================================================
    // 1️⃣  HIỂN THỊ DANH SÁCH & SẮP XẾP
    // ================================================================
    /**
     * Lấy danh sách xe có phân trang và sắp xếp.
     * Mặc định sắp xếp theo "maXe" giảm dần.
     */
    public PageResponseDTO<XeResponseDTO> getAllXe(
            int page, int size, String sortBy, String sortDirection) {

        // Mặc định sắp xếp theo mã xe (DESC)
        Sort sort = SortUtils.createSort(sortBy, sortDirection, "maXe", Sort.Direction.DESC);

        page = Math.max(0, page);
        size = Math.min(size, 100);
        size = Math.max(1, size);

        Pageable pageable = PageRequest.of(page, size, sort);

        // Lấy toàn bộ danh sách xe
        Page<Xe> xePage = xeRepository.findAll(pageable);

        // Map sang DTO
        return new PageResponseDTO<>(xePage.map(XeResponseDTO::new));
    }

    // ================================================================
    // 2️⃣  TÌM KIẾM & PHÂN TRANG & SẮP XẾP
    // ================================================================
    /**
     * Tìm kiếm xe theo các tiêu chí:
     * - Biển số (contains)
     * - Hãng xe (contains join)
     * - Năm sản xuất (equals)
     */
    public PageResponseDTO<XeResponseDTO> searchXe(
            int page, int size, String sortBy, String sortDirection,
            String bienSo, String hangXe, Integer namSanXuat,
            String mauSac, String trangThai) {

        Specification<Xe> spec = Specification.where(null);
        spec = spec.and(JpaSpecificationUtil.attributeContains("bienSo", bienSo));
        spec = spec.and(JpaSpecificationUtil.attributeContains("hangXe", hangXe));
        spec = spec.and(JpaSpecificationUtil.attributeEquals("namSanXuat", namSanXuat));
        spec = spec.and(JpaSpecificationUtil.attributeContains("mauSac", mauSac));
        spec = spec.and(JpaSpecificationUtil.attributeContains("trangThai", trangThai));

        Sort sort = SortUtils.createSort(sortBy, sortDirection, "maXe", Sort.Direction.DESC);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(size, 100), sort);

        Page<Xe> xePage = xeRepository.findAll(spec, pageable);
        return new PageResponseDTO<>(xePage.map(XeResponseDTO::new));
    }

    // HÀM CẬP NHẬT THÔNG TIN XE
    /**
     * Cập nhật thông tin xe, chỉ ghi đè các trường được gửi (Partial Update).
     */
    @Transactional
    public Xe updateXe(Integer maXe, XeUpdateDTO updateDTO) {

        // 1️⃣ Tìm Xe hiện tại
        Xe existingXe = xeRepository.findById(maXe)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Xe với Mã: " + maXe));

        // --- 2️⃣ Cập nhật Biển Số ---
        String newBienSo = updateDTO.getBienSo();
        if (newBienSo != null && !newBienSo.trim().isEmpty()) {
            // Kiểm tra trùng biển số (trừ chính nó)
            xeRepository.findByBienSo(newBienSo).ifPresent(xe -> {
                if (!xe.getMaXe().equals(maXe)) {
                    throw new ResourceAlreadyExistsException("Biển số xe đã tồn tại: " + newBienSo);
                }
            });
            existingXe.setBienSo(newBienSo.trim().toUpperCase());
        }

        // --- 3️⃣ Cập nhật Hãng Xe ---
        if (updateDTO.getHangXe() != null && !updateDTO.getHangXe().trim().isEmpty()) {
            existingXe.setHangXe(updateDTO.getHangXe().trim());
        }

        // --- 4️⃣ Cập nhật Dòng Xe ---
        if (updateDTO.getDongXe() != null && !updateDTO.getDongXe().trim().isEmpty()) {
            existingXe.setDongXe(updateDTO.getDongXe().trim());
        }

        // --- 5️⃣ Cập nhật Năm Sản Xuất ---
        if (updateDTO.getNamSanXuat() != null) {
            Integer year = updateDTO.getNamSanXuat();
            int currentYear = java.time.LocalDate.now().getYear();
            if (year < 2000 || year > currentYear) {
                throw new IllegalArgumentException("Năm sản xuất không hợp lệ. Phải từ 2000 đến " + currentYear);
            }
            existingXe.setNamSanXuat(year);
        }

        // --- 6️⃣ Cập nhật Màu Sắc ---
        if (updateDTO.getMauSac() != null && !updateDTO.getMauSac().trim().isEmpty()) {
            existingXe.setMauSac(updateDTO.getMauSac().trim());
        }

        // --- 7️⃣ Cập nhật Trạng Thái ---
        String newTrangThai = updateDTO.getTrangThai();
        if (newTrangThai != null && !newTrangThai.trim().isEmpty()) {
            String[] VALID_TRANG_THAI = {"Hoạt động", "Đang bảo trì", "Đã xóa"};
            if (!Arrays.asList(VALID_TRANG_THAI).contains(newTrangThai)) {
                throw new IllegalArgumentException("Trạng thái không hợp lệ. Chỉ chấp nhận: Hoạt động, Đang bảo trì, Đã xóa.");
            }
            existingXe.setTrangThai(newTrangThai);
        }

        // --- 8️⃣ Cập nhật Khách Hàng (nếu có) ---
        if (updateDTO.getMaKhachHang() != null) {
            KhachHang newKhachHang = khachHangRepository.findById(updateDTO.getMaKhachHang())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy Khách Hàng với Mã: " + updateDTO.getMaKhachHang()));
            existingXe.setKhachHang(newKhachHang);
        }

        // --- 9️⃣ Lưu và trả về ---
        return xeRepository.save(existingXe);
    }



}
