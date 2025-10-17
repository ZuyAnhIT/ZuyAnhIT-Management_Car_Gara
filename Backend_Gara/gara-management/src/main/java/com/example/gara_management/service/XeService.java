package com.example.gara_management.service;

import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.XeDTO.XeCreateDTO;
import com.example.gara_management.dto.XeDTO.XeResponseDTO;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;
import com.example.gara_management.model.KhachHang;
import com.example.gara_management.model.Xe;
import com.example.gara_management.repository.KhachHangRepository;
import com.example.gara_management.repository.XeRepository;
import com.example.gara_management.util.JpaSpecificationUtil;
import com.example.gara_management.util.SortUtils;

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
    private final KhachHangRepository khachHangRepository;

    public XeService(XeRepository xeRepository, KhachHangRepository khachHangRepository) {
        this.xeRepository = xeRepository;
        this.khachHangRepository = khachHangRepository;
    }
    // HÀM THÊM XE
    /**
     * Thêm mới xe, tự động gán Khách Hàng theo MãKhachHang.
     */
    @Transactional
    public Xe addXe(XeCreateDTO createDTO) {

        // 1️⃣ Kiểm tra biển số xe đã tồn tại chưa
        xeRepository.findByBienSo(createDTO.getBienSo()).ifPresent(xe -> {
            throw new ResourceAlreadyExistsException("Biển số xe đã tồn tại: " + createDTO.getBienSo());
        });

        // 2️⃣ Tìm kiếm Khách Hàng theo mã
        Integer maKhachHang = createDTO.getMaKhachHang();
        KhachHang khachHang = khachHangRepository.findById(maKhachHang)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy Khách Hàng với Mã: " + maKhachHang));

        // 3️⃣ Chuyển đổi DTO sang Entity và thiết lập mối quan hệ
        Xe newXe = new Xe(
                createDTO.getBienSo(),
                createDTO.getHangXe(),
                createDTO.getDongXe(),
                createDTO.getNamSanXuat(),
                createDTO.getMauSac(),
                khachHang // Thiết lập Entity Khách Hàng
        );

        // 4️⃣ Nếu có trạng thái gửi lên thì cập nhật (nếu null → giữ "Hoạt động")
        if (createDTO.getTrangThai() != null && !createDTO.getTrangThai().isBlank()) {
            newXe.setTrangThai(createDTO.getTrangThai());
        }

        // 5️⃣ Lưu vào Database
        return xeRepository.save(newXe);
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


}
