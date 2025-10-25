package com.example.gara_management.service;

import com.example.gara_management.dto.DichVuDTO.DichVuCreateDTO;
import com.example.gara_management.dto.DichVuDTO.DichVuResponseDTO;
import com.example.gara_management.dto.DichVuDTO.DichVuStatisticsDTO;
import com.example.gara_management.dto.DichVuDTO.DichVuUpdateDTO;
import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;
import com.example.gara_management.model.DichVu;
import com.example.gara_management.model.LoaiDichVu;
import com.example.gara_management.repository.DichVuRepository;
import com.example.gara_management.repository.LoaiDichVuRepository;
import com.example.gara_management.util.FileUploadUtil; // <-- Sử dụng lại
import com.example.gara_management.util.JpaSpecificationUtil; 
import com.example.gara_management.util.SortUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DichVuService {
    
    private final DichVuRepository dichVuRepository;
    private final LoaiDichVuRepository loaiDichVuRepository;
    
    private static final List<String> VALID_TRANG_THAI = Arrays.asList("Còn hàng", "Hết hàng", "Đã xóa", "Sắp hết");

    // --- HÀM CONVERT ---
    private DichVuResponseDTO convertToDTO(DichVu dichVu) {
        return new DichVuResponseDTO(dichVu); 
    }
    
    // --- THÊM MỚI DỊCH VỤ ---
    @Transactional
    public DichVu addService(DichVuCreateDTO createDTO, MultipartFile imageFile) {
        
        dichVuRepository.findByTenDichVu(createDTO.getTenDichVu()).ifPresent(dichVu -> {
            throw new ResourceAlreadyExistsException("Tên dịch vụ đã tồn tại: " + createDTO.getTenDichVu());
        });
        
        LoaiDichVu loaiDichVu = loaiDichVuRepository.findByTenLoai(createDTO.getTenLoaiDichVu())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Loại Dịch Vụ: " + createDTO.getTenLoaiDichVu()));
        
        String uniqueFilename = null;
        try {
            // LƯU FILE VÀ LẤY TÊN FILE DUY NHẤT
            uniqueFilename = FileUploadUtil.saveImageFile(imageFile);
        } catch (IOException | IllegalArgumentException e) { 
            throw new RuntimeException("Lỗi khi xử lý ảnh: " + e.getMessage(), e);
        }

        // Tạo entity DichVu
        DichVu newService = new DichVu(); 
        newService.setTenDichVu(createDTO.getTenDichVu());
        newService.setMoTa(createDTO.getMoTa());
        newService.setAnhDichVu(uniqueFilename); // <-- Lưu TÊN FILE
        newService.setSoLuongTon(createDTO.getSoLuongTon());
        newService.setSoLuongBan(createDTO.getSoLuongBan());
        newService.setGia(createDTO.getGia());
        newService.setThoiGianUocTinh(createDTO.getThoiGianUocTinh());
        newService.setTrangThai("Còn hàng"); 
        newService.setNgayTao(LocalDateTime.now());
        newService.setLoaiDichVu(loaiDichVu);
        
        return dichVuRepository.save(newService);
    }
    
    // --- CẬP NHẬT DỊCH VỤ ---
    @Transactional
    public DichVu updateService(Integer maDichVu, DichVuUpdateDTO updateDTO, MultipartFile imageFile) {
        
        DichVu dichVu = dichVuRepository.findById(maDichVu)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ với mã: " + maDichVu));
        
        // 1. Cập nhật Tên Dịch Vụ
        String newTenDichVu = updateDTO.getTenDichVu();
        if (newTenDichVu != null && !newTenDichVu.trim().isEmpty()) {
             if (!dichVu.getTenDichVu().equalsIgnoreCase(newTenDichVu)) {
                dichVuRepository.findByTenDichVu(newTenDichVu).ifPresent(existing -> {
                    if (!existing.getMaDichVu().equals(maDichVu)) {
                         throw new ResourceAlreadyExistsException("Tên dịch vụ đã tồn tại: " + newTenDichVu);
                    }
                });
                dichVu.setTenDichVu(newTenDichVu);
             }
        }
        
        // 2. Cập nhật Loại Dịch Vụ
        String newTenLoaiDichVu = updateDTO.getTenLoaiDichVu();
        if (newTenLoaiDichVu != null && !newTenLoaiDichVu.trim().isEmpty()) {
            LoaiDichVu newLoaiDichVu = loaiDichVuRepository.findByTenLoai(newTenLoaiDichVu)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Loại Dịch Vụ: " + newTenLoaiDichVu));
            
            if ("Đã xóa".equalsIgnoreCase(newLoaiDichVu.getTrangThai())) {
                throw new IllegalArgumentException("Không thể gán Dịch Vụ cho Loại Dịch Vụ đang ở trạng thái 'Đã xóa'.");
            }
            dichVu.setLoaiDichVu(newLoaiDichVu);
        }

        // 3. Xử lý Trạng Thái Dịch Vụ
        String newTrangThai = updateDTO.getTrangThai();
        if (newTrangThai != null && !newTrangThai.trim().isEmpty()) {
            if (!VALID_TRANG_THAI.contains(newTrangThai)) {
                throw new IllegalArgumentException("Trạng thái không hợp lệ. Chỉ chấp nhận: " + String.join(", ", VALID_TRANG_THAI));
            }
            dichVu.setTrangThai(newTrangThai);
        }
        
        // 4. Xử lý Ảnh mới (Nếu có file mới được gửi)
        if (imageFile != null && !imageFile.isEmpty()) {
            // Xóa ảnh cũ
            if (dichVu.getAnhDichVu() != null) {
                FileUploadUtil.deleteImageFile(dichVu.getAnhDichVu());
            }
            // Lưu ảnh mới
            try {
                String newUniqueFilename = FileUploadUtil.saveImageFile(imageFile);
                dichVu.setAnhDichVu(newUniqueFilename);
            } catch (IOException | IllegalArgumentException e) {
                 throw new RuntimeException("Lỗi khi xử lý ảnh mới: " + e.getMessage(), e);
            }
        }
        
        // 5. Cập nhật các trường còn lại
        Optional.ofNullable(updateDTO.getMoTa()).ifPresent(dichVu::setMoTa);
        Optional.ofNullable(updateDTO.getSoLuongTon()).ifPresent(dichVu::setSoLuongTon);
        Optional.ofNullable(updateDTO.getSoLuongBan()).ifPresent(dichVu::setSoLuongBan);
        Optional.ofNullable(updateDTO.getGia()).ifPresent(dichVu::setGia);
        Optional.ofNullable(updateDTO.getThoiGianUocTinh()).ifPresent(dichVu::setThoiGianUocTinh);

        return dichVuRepository.save(dichVu);
    }
    // HIỂN THỊ DANH SÁCH & SẮP XẾP
    // --- Phương thức 1: CHỈ HIỂN THỊ DANH SÁCH & SẮP XẾP ---
    /**
     * Lấy danh sách dịch vụ có phân trang và sắp xếp. Mặc định sắp xếp theo ngày
     * tạo giảm dần.
     */
    public PageResponseDTO<DichVuResponseDTO> getAllServices(
            int page, int size, String sortBy, String sortDirection) {

        // Mặc định: sắp xếp theo "ngayTao" giảm dần (DESC)
        Sort sort = SortUtils.createSort(
                sortBy,
                sortDirection,
                "ngayTao",
                Sort.Direction.DESC);

        page = Math.max(0, page);
        size = Math.min(size, 100);
        size = Math.max(1, size);

        Pageable pageable = PageRequest.of(page, size, sort);

        // Gọi findAll KHÔNG có Specification
        Page<DichVu> dichVuPage = dichVuRepository.findAll(pageable);

        // Chuyển đổi sang DTO (để hiển thị Tên Loại Dịch Vụ)
        return new PageResponseDTO<>(dichVuPage.map(DichVuResponseDTO::new));
    }


    // HÀM TÌM KIẾM & PHÂN TRANG & SẮP XẾP (Tách biệt) ---
    /**
     * Tìm kiếm dịch vụ với các tiêu chí lọc.
     */
    public PageResponseDTO<DichVuResponseDTO> searchServices(
            int page, int size, String sortBy, String sortDirection,
            String tenDichVu, String tenLoai) {
        
        // 1. Xây dựng Specification
        Specification<DichVu> spec = Specification.where(null); 
        
        spec = spec.and(JpaSpecificationUtil.attributeContains("tenDichVu", tenDichVu));
        spec = spec.and(JpaSpecificationUtil.attributeContainsJoin("loaiDichVu", "tenLoai", tenLoai));
        // 2. Xây dựng Pageable
        Sort sort = SortUtils.createSort(sortBy, sortDirection, "ngayTao", Sort.Direction.DESC);
        page = Math.max(0, page);
        size = Math.min(size, 100); 
        size = Math.max(1, size);
        
        Pageable pageable = PageRequest.of(page, size, sort);

        // 3. Gọi Repository để tìm kiếm (sử dụng Specification và Pageable)
        Page<DichVu> dichVuPage = dichVuRepository.findAll(spec, pageable);

        // 4. Chuyển đổi sang DTO và trả về
        return new PageResponseDTO<>(dichVuPage.map(DichVuResponseDTO::new));
    }


    // HÀM XÓA MỀM DỊCH VỤ
    /**
     * Thực hiện xóa mềm (Soft Delete) Dịch Vụ bằng cách thay đổi trạng thái sang "Đã xóa".
     * @param maDichVu Mã dịch vụ cần xóa.
     * @return DichVu đã được cập nhật trạng thái.
     * @throws ResourceNotFoundException Nếu không tìm thấy dịch vụ.
     * @throws IllegalStateException Nếu dịch vụ đã ở trạng thái "Đã xóa".
     */
    @Transactional
    public DichVu softDeleteService(Integer maDichVu) {
        
        //  Giữ logic trong Service và gọi lại từ hàm tiện ích
        
        // Hoặc cách đơn giản: giữ nguyên code cũ (đã được viết trước đó)
        DichVu entity = dichVuRepository.findById(maDichVu)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Dịch Vụ với Mã: " + maDichVu));

        if ("Đã xóa".equals(entity.getTrangThai())) {
            throw new IllegalStateException("Loại dịch vụ này đã ở trạng thái 'Đã xóa' và không thể xóa tiếp.");
        }
        
        entity.setTrangThai("Đã xóa");
        return dichVuRepository.save(entity);
    }

    @Transactional(readOnly = true)
public DichVuStatisticsDTO getDichVuStatistics() {
    long total = dichVuRepository.count();
    long sapHet = dichVuRepository.countByTrangThai("Sắp hết");
    long conHang = dichVuRepository.countByTrangThai("Còn hàng");
    long hetHang = dichVuRepository.countByTrangThai("Hết hàng");

    // Tổng số lượng tồn
    long tongSoLuongTon = dichVuRepository.findAll()
                                .stream()
                                .mapToLong(DichVu::getSoLuongTon)
                                .sum();

    // Tổng giá trị tồn kho
    BigDecimal tongGiaTriTonKho = dichVuRepository.findAll()
                                .stream()
                                .map(d -> d.getGia().multiply(BigDecimal.valueOf(d.getSoLuongTon())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

    return new DichVuStatisticsDTO(total, sapHet, conHang, hetHang, tongSoLuongTon, tongGiaTriTonKho);
}    

}