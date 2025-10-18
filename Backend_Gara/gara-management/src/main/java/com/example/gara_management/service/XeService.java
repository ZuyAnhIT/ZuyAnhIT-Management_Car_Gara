package com.example.gara_management.service;

import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.XeDTO.XeCreateDTO;
import com.example.gara_management.dto.XeDTO.XeResponseDTO;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;
import com.example.gara_management.model.KhachHang;
import com.example.gara_management.repository.KhachHangRepository;
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
import org.springframework.transaction.annotation.Transactional;

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

    // ================================================================
    // 3️⃣  THÊM MỚI XE
    // ================================================================
    /**
     * Tạo mới một xe (Create)
     * Kiểm tra trùng biển số và liên kết với khách hàng nếu có.
     */
    @Transactional
    public Xe createXe(XeCreateDTO createDTO) {

        // 1. Kiểm tra trùng biển số
        xeRepository.findByBienSo(createDTO.getBienSo()).ifPresent(xe -> {
            throw new ResourceAlreadyExistsException("Biển số xe đã tồn tại: " + createDTO.getBienSo());
        });

        // 2. Nếu có mã khách hàng thì tìm trong DB (nếu không có, khachHang giữ null)
        KhachHang khachHang = null;
        if (createDTO.getMaKhachHang() != null) {
            khachHang = khachHangRepository.findById(createDTO.getMaKhachHang())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy Khách hàng với mã: " + createDTO.getMaKhachHang()));
        }

        // 3. Khởi tạo đối tượng Xe mới
        Xe newXe = new Xe();
        newXe.setBienSo(createDTO.getBienSo());
        newXe.setHangXe(createDTO.getHangXe());
        newXe.setDongXe(createDTO.getDongXe());
        newXe.setNamSanXuat(createDTO.getNamSanXuat());
        newXe.setMauSac(createDTO.getMauSac());
        newXe.setTrangThai("Hoạt động"); // mặc định
        newXe.setKhachHang(khachHang);   // có thể null nếu không nhập

        // 4. Lưu vào DB
        return xeRepository.save(newXe);
    }


}
