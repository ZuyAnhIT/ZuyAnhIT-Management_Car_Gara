package com.example.gara_management.service;

import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.DichVuDTO.DichVuCreateDTO;
import com.example.gara_management.dto.DichVuDTO.DichVuResponseDTO;
import com.example.gara_management.dto.DichVuDTO.DichVuUpdateDTO;
import com.example.gara_management.model.DichVu;
import com.example.gara_management.model.LoaiDichVu;
import com.example.gara_management.repository.DichVuRepository;
import com.example.gara_management.repository.LoaiDichVuRepository;
import com.example.gara_management.util.JpaSpecificationUtil;
import com.example.gara_management.util.SortUtils;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
@Service
public class DichVuService {

    private final DichVuRepository dichVuRepository;
    private final LoaiDichVuRepository loaiDichVuRepository; // Cần dùng Repository của LoaiDichVu
    private static final String[] VALID_TRANG_THAI = {"Còn hàng", "Hết hàng", "Đã xóa"};
    public DichVuService(DichVuRepository dichVuRepository, LoaiDichVuRepository loaiDichVuRepository) {
        this.dichVuRepository = dichVuRepository;
        this.loaiDichVuRepository = loaiDichVuRepository;
    }

    // HÀM THÊM DỊCH VỤ
    /**
     * Thêm mới dịch vụ, tự động tìm MaLoai từ TenLoaiDichVu.
     */
    @Transactional
    public DichVu addService(DichVuCreateDTO createDTO) {

        // 1. Kiểm tra tên dịch vụ đã tồn tại chưa
        dichVuRepository.findByTenDichVu(createDTO.getTenDichVu()).ifPresent(dichVu -> {
            throw new ResourceAlreadyExistsException("Tên dịch vụ đã tồn tại: " + createDTO.getTenDichVu());
        });

        // 2. Tìm kiếm LoaiDichVu theo Tên
        String tenLoai = createDTO.getTenLoaiDichVu();
        LoaiDichVu loaiDichVu = loaiDichVuRepository.findByTenLoai(tenLoai)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Loại Dịch Vụ với Tên: " + tenLoai));

        // 3. Chuyển đổi DTO sang Entity và thiết lập mối quan hệ
        DichVu newService = new DichVu(
                createDTO.getTenDichVu(),
                createDTO.getMoTa(),
                createDTO.getAnhDichVu(),
                createDTO.getSoLuongTon(),
                createDTO.getSoLuongBan(),
                createDTO.getGia(),
                createDTO.getThoiGianUocTinh(),
                loaiDichVu // Thiết lập Entity LoaiDichVu
        );

        // 4. Lưu vào Database
        return dichVuRepository.save(newService);
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


    //HÀM CẬP NHẬT THÔNG TIN DỊCH VỤ
    /**
     * Cập nhật thông tin dịch vụ, chỉ ghi đè các trường được gửi (Partial Update).
     */
    @Transactional
    public DichVu updateService(Integer maDichVu, DichVuUpdateDTO updateDTO) {
        
        // 1. Tìm Dịch vụ hiện tại
        DichVu existingService = dichVuRepository.findById(maDichVu)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Dịch Vụ với Mã: " + maDichVu));

        // --- 2. Cập nhật Tên Dịch Vụ ---
        String newTenDichVu = updateDTO.getTenDichVu();
        if (newTenDichVu != null && !newTenDichVu.trim().isEmpty()) {
            
            // Kiểm tra trùng tên (trừ chính nó)
            dichVuRepository.findByTenDichVu(newTenDichVu).ifPresent(dichVu -> {
                if (!dichVu.getMaDichVu().equals(maDichVu)) {
                    throw new ResourceAlreadyExistsException("Tên dịch vụ đã tồn tại: " + newTenDichVu);
                }
            });
            existingService.setTenDichVu(newTenDichVu);
        }

        // --- 3. Cập nhật Loại Dịch Vụ (Tìm theo Tên) ---
        String newTenLoaiDichVu = updateDTO.getTenLoaiDichVu();
        if (newTenLoaiDichVu != null && !newTenLoaiDichVu.trim().isEmpty()) {
            
            LoaiDichVu newLoaiDichVu = loaiDichVuRepository.findByTenLoai(newTenLoaiDichVu)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Loại Dịch Vụ với Tên: " + newTenLoaiDichVu));
            
            // Kiểm tra trạng thái Loại Dịch Vụ: KHÔNG được là "Đã xóa"
            if ("Đã xóa".equals(newLoaiDichVu.getTrangThai())) {
                throw new IllegalArgumentException("Không thể gán Dịch Vụ cho Loại Dịch Vụ đang ở trạng thái 'Đã xóa'.");
            }
            
            existingService.setLoaiDichVu(newLoaiDichVu);
        }

        // --- 4. Cập nhật Trạng Thái Dịch Vụ ---
        String newTrangThai = updateDTO.getTrangThai();
        if (newTrangThai != null && !newTrangThai.trim().isEmpty()) {
            
            // Kiểm tra trạng thái hợp lệ
            if (!Arrays.asList(VALID_TRANG_THAI).contains(newTrangThai)) {
                throw new IllegalArgumentException("Trạng thái không hợp lệ. Chỉ chấp nhận: " + String.join(", ", VALID_TRANG_THAI));
            }
            
            existingService.setTrangThai(newTrangThai);
        }

        // --- 5. Cập nhật các trường còn lại (Nếu không null) ---
        Optional.ofNullable(updateDTO.getMoTa()).ifPresent(existingService::setMoTa);
        Optional.ofNullable(updateDTO.getAnhDichVu()).ifPresent(existingService::setAnhDichVu);
        Optional.ofNullable(updateDTO.getSoLuongTon()).ifPresent(existingService::setSoLuongTon);
        Optional.ofNullable(updateDTO.getSoLuongBan()).ifPresent(existingService::setSoLuongBan);
        Optional.ofNullable(updateDTO.getGia()).ifPresent(existingService::setGia);
        Optional.ofNullable(updateDTO.getThoiGianUocTinh()).ifPresent(existingService::setThoiGianUocTinh);

        // 6. Lưu và trả về
        return dichVuRepository.save(existingService);
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





    

}