package com.example.gara_management.service;

import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.XeDTO.XeCreateDTO;
import com.example.gara_management.dto.XeDTO.XeResponseDTO;
import com.example.gara_management.dto.XeDTO.XeUpdateDTO;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class XeService {

    private final XeRepository xeRepository;
    private final KhachHangRepository khachHangRepository;

    public XeService(XeRepository xeRepository, KhachHangRepository khachHangRepository) {
        this.xeRepository = xeRepository;
        this.khachHangRepository = khachHangRepository;
    }

    // ================================================================
    // HIỂN THỊ DANH SÁCH & SẮP XẾP
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
    // TÌM KIẾM & PHÂN TRANG & SẮP XẾP
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
    // THÊM MỚI XE
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
        newXe.setKhachHang(khachHang); // có thể null nếu không nhập

        // 4. Lưu vào DB
        return xeRepository.save(newXe);
    }

    // ================================================================
    // CẬP NHẬT THÔNG TIN XE (UPDATE)
    // ================================================================
    /**
     * Cập nhật thông tin xe (Partial Update)
     * Chỉ ghi đè các trường có giá trị trong DTO.
     */
    @Transactional
    public Xe updateXe(Integer maXe, XeUpdateDTO updateDTO) {

        // 1. Tìm xe hiện tại
        Xe existingXe = xeRepository.findById(maXe)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Xe với mã: " + maXe));

        // 2. Kiểm tra và cập nhật Biển số (NẾU ĐƯỢC GỬI)
        // Logic này đã đúng: chỉ chạy khi newBienSo != null và không rỗng.
        String newBienSo = updateDTO.getBienSo();
        if (newBienSo != null && !newBienSo.trim().isEmpty()) {
            xeRepository.findByBienSo(newBienSo).ifPresent(xe -> {
                if (!xe.getMaXe().equals(maXe)) {
                    throw new ResourceAlreadyExistsException("Biển số xe đã tồn tại: " + newBienSo);
                }
            });
            existingXe.setBienSo(newBienSo);
        }

        // 3. Cập nhật Hãng xe
        if (updateDTO.getHangXe() != null && !updateDTO.getHangXe().trim().isEmpty()) {
            existingXe.setHangXe(updateDTO.getHangXe());
        }

        // 4. Cập nhật Dòng xe
        if (updateDTO.getDongXe() != null && !updateDTO.getDongXe().trim().isEmpty()) {
            existingXe.setDongXe(updateDTO.getDongXe());
        }

        // 5. Cập nhật Năm sản xuất
        if (updateDTO.getNamSanXuat() != null && updateDTO.getNamSanXuat() >= 1886) {
            existingXe.setNamSanXuat(updateDTO.getNamSanXuat());
        }

        // 6. Cập nhật Màu sắc
        if (updateDTO.getMauSac() != null && !updateDTO.getMauSac().trim().isEmpty()) {
            existingXe.setMauSac(updateDTO.getMauSac());
        }

        // 7. Cập nhật Trạng thái (nếu hợp lệ)
        if (updateDTO.getTrangThai() != null && !updateDTO.getTrangThai().trim().isEmpty()) {
            String newStatus = updateDTO.getTrangThai().trim();
            if (!Arrays.asList("Hoạt động", "Bảo trì", "Ngưng sử dụng", "Đã xóa").contains(newStatus)) {
                throw new IllegalArgumentException(
                        "Trạng thái không hợp lệ. Chỉ chấp nhận: Hoạt động, Bảo trì, Ngưng sử dụng, Đã xóa.");
            }
            existingXe.setTrangThai(newStatus);
        }

        // 8. Cập nhật Khách hàng nếu có
        if (updateDTO.getMaKhachHang() != null) {
            KhachHang kh = khachHangRepository.findById(updateDTO.getMaKhachHang())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy Khách hàng với mã: " + updateDTO.getMaKhachHang()));
            existingXe.setKhachHang(kh);
        }

        // 9. Lưu và trả về
        return xeRepository.save(existingXe);
    }

    // =======================================
    // HÀM XÓA MỀM XE
    // =======================================
    /**
     * Xóa mềm (Soft Delete) xe bằng cách đổi trạng thái sang "Đã xóa".
     * 
     * @param maXe Mã xe cần xóa
     * @return Xe sau khi cập nhật trạng thái
     * @throws ResourceNotFoundException Nếu không tìm thấy xe
     * @throws IllegalStateException     Nếu xe đã ở trạng thái "Đã xóa"
     */
    @Transactional
    public Xe softDeleteXe(Integer maXe) {

        // 1 Tìm xe theo mã
        Xe existingXe = xeRepository.findById(maXe)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với mã: " + maXe));

        // 2 Kiểm tra trạng thái hiện tại
        if ("Đã xóa".equalsIgnoreCase(existingXe.getTrangThai())) {
            throw new IllegalStateException("Xe này đã ở trạng thái 'Đã xóa' và không thể xóa thêm.");
        }

        // 3 Cập nhật trạng thái sang "Đã xóa"
        existingXe.setTrangThai("Đã xóa");

        // 4 Lưu vào cơ sở dữ liệu
        return xeRepository.save(existingXe);
    }
    // 🔹 Thống kê xe tổng và xe hoạt động
    public Map<String, Long> thongKeXe() {
        Map<String, Long> result = new HashMap<>();

        long tongXe = xeRepository.count();
        long xeHoatDong = xeRepository.countByTrangThai("Hoạt động");

        result.put("tongSoXe", tongXe);
        result.put("soXeHoatDong", xeHoatDong);

        return result;
    }
}
