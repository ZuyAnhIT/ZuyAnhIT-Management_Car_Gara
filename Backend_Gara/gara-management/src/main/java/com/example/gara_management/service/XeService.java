package com.example.gara_management.service;

import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.XeDTO.XeResponseDTO;
import com.example.gara_management.exception.ResourceNotFoundException;
import com.example.gara_management.model.Xe;
import com.example.gara_management.repository.XeRepository;
import com.example.gara_management.util.JpaSpecificationUtil;
import com.example.gara_management.util.SortUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class XeService {

    private final XeRepository xeRepository;

    public XeService(XeRepository xeRepository) {
        this.xeRepository = xeRepository;
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


    // ================================================================
    // 3️⃣ XÓA MỀM XE
    // ================================================================
    /**
     * Thực hiện xóa mềm (soft delete) xe bằng cách đổi trạng thái sang "Đã xóa".
     * @param maXe Mã xe cần xóa
     * @return Xe đã được cập nhật trạng thái
     * @throws ResourceNotFoundException nếu không tìm thấy xe
     * @throws IllegalStateException nếu xe đã ở trạng thái "Đã xóa"
     */
    @Transactional
    public Xe softDeleteXe(Integer maXe) {
        Xe entity = xeRepository.findById(maXe)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Xe với Mã: " + maXe));

        if ("Đã xóa".equalsIgnoreCase(entity.getTrangThai())) {
            throw new IllegalStateException("Xe này đã ở trạng thái 'Đã xóa' và không thể xóa tiếp.");
        }

        entity.setTrangThai("Đã xóa");
        return xeRepository.save(entity);
    }
}
