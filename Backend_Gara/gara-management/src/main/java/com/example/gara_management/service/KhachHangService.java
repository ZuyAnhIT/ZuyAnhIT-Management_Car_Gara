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
import java.util.HashMap;
import java.util.Map;
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

    // ----------------------------------------------------------------------
    // --- PHƯƠNG THỨC 1: CHỈ HIỂN THỊ DANH SÁCH & SẮP XẾP ---
    // ----------------------------------------------------------------------
    /**
     * Lấy danh sách khách hàng có phân trang và sắp xếp.
     * Mặc định sắp xếp theo NgayTao giảm dần.
     */
    public PageResponseDTO<KhachHangResponseDTO> getAllKhachHang(
            int page, int size, String sortBy, String sortDirection) {
        
        // Cập nhật: Mặc định sắp xếp theo ngayTao (giảm dần)
        Sort sort = SortUtils.createSort(
            sortBy, 
            sortDirection, 
            "maKhachHang", // Giữ nguyên "maKhachHang" ở đây nếu "ngayTao" không phải là trường của KhachHang.
                           // LƯU Ý: Khách hàng không có NgayTao trong Model, nên tôi giữ lại maKhachHang.
                           // Nếu bạn thêm NgayTao vào Model KhachHang, hãy đổi nó thành "ngayTao".
            Sort.Direction.DESC 
        );

        page = Math.max(0, page);
        size = Math.min(size, 100); 
        size = Math.max(1, size);
        
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<KhachHang> khachHangPage = khachHangRepository.findAll(pageable);

        return new PageResponseDTO<>(khachHangPage.map(KhachHangResponseDTO::new));
    }

    // ----------------------------------------------------------------------
    // --- PHƯƠNG THỨC 2: TÌM KIẾM, LỌC & PHÂN TRANG (Tách biệt) ---
    // ----------------------------------------------------------------------
    /**
     * Tìm kiếm khách hàng theo Tên, SĐT, Email, Trạng thái và Loại khách.
     */
    public PageResponseDTO<KhachHangResponseDTO> searchKhachHang(
            int page, int size, String sortBy, String sortDirection,
            String tenKhachHang, String soDienThoai, String email, 
            String trangThai, String loaiKhach) { 
        
        // 1. Xây dựng Specification
        Specification<KhachHang> spec = Specification.where(null); 
        
        // Tìm kiếm theo Tên Khách Hàng (LIKE)
        spec = spec.and(JpaSpecificationUtil.attributeContains("tenKhachHang", tenKhachHang));
        
        // Tìm kiếm theo Số Điện Thoại (LIKE)
        spec = spec.and(JpaSpecificationUtil.attributeContains("soDienThoai", soDienThoai));
        
        // Tìm kiếm theo Email (LIKE)
        spec = spec.and(JpaSpecificationUtil.attributeContains("email", email));
        
        // Lọc theo Trạng thái (chính xác)
        spec = spec.and(JpaSpecificationUtil.attributeEquals("trangThai", trangThai));
        
        // Lọc theo Loại Khách (chính xác)
        spec = spec.and(JpaSpecificationUtil.attributeEquals("loaiKhach", loaiKhach));

        // 2. Xây dựng Pageable
        // Mặc định sắp xếp theo maKhachHang (Giảm dần)
        Sort sort = SortUtils.createSort(sortBy, sortDirection, "maKhachHang", Sort.Direction.DESC);
        page = Math.max(0, page);
        size = Math.min(size, 100); 
        size = Math.max(1, size);
        
        Pageable pageable = PageRequest.of(page, size, sort);

        // 3. Gọi Repository để tìm kiếm (sử dụng Specification và Pageable)
        Page<KhachHang> khachHangPage = khachHangRepository.findAll(spec, pageable);

        // 4. Chuyển đổi sang DTO và trả về
        return new PageResponseDTO<>(khachHangPage.map(KhachHangResponseDTO::new));
    }

    // 🧠 UPDATE
    /**
     * Cập nhật thông tin khách hàng, chỉ ghi đè các trường được gửi (Partial Update).
     * @param maKhachHang Mã khách hàng cần sửa.
     * @param updateDTO Dữ liệu cập nhật.
     * @return KhachHang đã được cập nhật.
     */
    @Transactional
    public KhachHang updateKhachHang(Integer maKhachHang, KhachHangUpdateDTO updateDTO) {
        
        // 1. Tìm Khách hàng hiện tại
        KhachHang existingKH = khachHangRepository.findById(maKhachHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Khách Hàng với Mã: " + maKhachHang));

        // --- 2. Cập nhật Số Điện Thoại (Kiểm tra trùng) ---
        String newSdt = updateDTO.getSoDienThoai();
        if (newSdt != null && !newSdt.trim().isEmpty()) {
            khachHangRepository.findBySoDienThoai(newSdt).ifPresent(kh -> {
                if (!kh.getMaKhachHang().equals(maKhachHang)) {
                    throw new ResourceAlreadyExistsException("Số điện thoại đã được sử dụng bởi khách hàng khác: " + newSdt);
                }
            });
            existingKH.setSoDienThoai(newSdt);
        }

        // --- 3. Cập nhật Email (Kiểm tra trùng) ---
        String newEmail = updateDTO.getEmail();
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            khachHangRepository.findByEmail(newEmail).ifPresent(kh -> {
                if (!kh.getMaKhachHang().equals(maKhachHang)) {
                    throw new ResourceAlreadyExistsException("Email đã được sử dụng bởi khách hàng khác: " + newEmail);
                }
            });
            existingKH.setEmail(newEmail);
        }
        
        // --- 4. Cập nhật các trường còn lại (Sử dụng Optional để kiểm tra null) ---
        // Tên Khách Hàng
        Optional.ofNullable(updateDTO.getTenKhachHang())
                .filter(s -> !s.trim().isEmpty())
                .ifPresent(existingKH::setTenKhachHang);
        
        // Địa Chỉ
        Optional.ofNullable(updateDTO.getDiaChi())
                .filter(s -> !s.trim().isEmpty())
                .ifPresent(existingKH::setDiaChi);

        // Loại Khách
        Optional.ofNullable(updateDTO.getLoaiKhach())
                .filter(s -> !s.trim().isEmpty())
                .ifPresent(existingKH::setLoaiKhach);

        // Ghi Chú
        // Ghi chú có thể là chuỗi rỗng để xóa nội dung
        Optional.ofNullable(updateDTO.getGhiChu()).ifPresent(existingKH::setGhiChu);

        // Trạng Thái
        Optional.ofNullable(updateDTO.getTrangThai())
                .filter(s -> !s.trim().isEmpty())
                .ifPresent(existingKH::setTrangThai);
        
        // 5. Lưu và trả về
        return khachHangRepository.save(existingKH);
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

    // Tính tổng số khách hàng trong hệ thống
    public long countAllCustomers() {
        return khachHangRepository.count();
    }

    // Tính số khách hàng theo loại
    public Map<String, Long> countCustomersByType() {
        Map<String, Long> result = new HashMap<>();

        long doanhNghiep = khachHangRepository.countByLoaiKhach("Doanh nghiệp");
        long caNhan = khachHangRepository.countByLoaiKhach("Cá nhân");

        result.put("soKhachHangDoanhNghiep", doanhNghiep);
        result.put("soKhachHangCaNhan", caNhan);
        result.put("tongSoKhachHang", doanhNghiep + caNhan);

        return result;
    }
}
