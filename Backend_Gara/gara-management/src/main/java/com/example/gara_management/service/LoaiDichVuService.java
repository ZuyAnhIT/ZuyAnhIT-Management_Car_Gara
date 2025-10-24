package com.example.gara_management.service;

import com.example.gara_management.model.LoaiDichVu;
import com.example.gara_management.repository.LoaiDichVuRepository;
import com.example.gara_management.util.JpaSpecificationUtil;
import com.example.gara_management.util.SortUtils;
import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.LoaiDichVuDTO.LoaiDichVuCreateDTO;
import com.example.gara_management.dto.LoaiDichVuDTO.LoaiDichVuResponseDTO;
import com.example.gara_management.dto.LoaiDichVuDTO.LoaiDichVuUpdateDTO;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;

import org.springframework.transaction.annotation.Transactional;

// Hỗ trợ phân trang
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class LoaiDichVuService {

    private final LoaiDichVuRepository loaiDichVuRepository;

    // Dependency Injection qua Constructor
    public LoaiDichVuService(LoaiDichVuRepository loaiDichVuRepository) {
        this.loaiDichVuRepository = loaiDichVuRepository;
    }


    // HÀM THÊM MỚI LOẠI DỊCH VỤ
    /**
     * Thêm mới một loại dịch vụ.
     * @param createDTO Dữ liệu đầu vào từ Controller.
     * @return LoaiDichVu đã được lưu (có MaLoai tự động tạo).
     * @throws ResourceAlreadyExistsException Nếu tên loại dịch vụ đã tồn tại.
     */
    @Transactional
    public LoaiDichVu themLoaiDichVu(LoaiDichVuCreateDTO createDTO) {
        String tenLoai = createDTO.getTenLoai();

        // 1. Kiểm tra tên loại dịch vụ đã tồn tại chưa
        Optional<LoaiDichVu> existingType = loaiDichVuRepository.findByTenLoai(tenLoai);
        if (existingType.isPresent()) {
            // Thay thế IllegalArgumentException bằng một Exception nghiệp vụ tùy chỉnh.
            throw new ResourceAlreadyExistsException("Tên loại dịch vụ đã tồn tại: " + tenLoai);
        }

        // 2. Chuyển đổi DTO sang Entity. 
        // MaLoai và NgayTao sẽ được DB/JPA tự động tạo/đặt mặc định.
        LoaiDichVu newLoaiDichVu = new LoaiDichVu(tenLoai); // Sử dụng constructor (String tenLoai)

        // 3. Lưu Entity vào Database và trả về Entity đã được lưu (có MaLoai)
        return loaiDichVuRepository.save(newLoaiDichVu);
    }


    // HÀM HIỂN THỊ DANH SÁCH CÓ PHÂN TRANG VÀ SẮP XẾP
     /**
     * Lấy danh sách loại dịch vụ có phân trang và sắp xếp tùy chỉnh.
     * @param page Trang hiện tại (0-based)
     * @param size Kích thước trang
     * @param sortBy Trường để sắp xếp (ví dụ: "ngayTao", "tenLoai")
     * @param sortDirection Hướng sắp xếp ("asc" hoặc "desc")
     * @return PageResponseDTO
     */
    public PageResponseDTO<LoaiDichVuResponseDTO> getAllServiceTypes(
            int page, int size, String sortBy, String sortDirection) {
        
        // Sử dụng SortUtils để tái sử dụng logic sắp xếp
        // Mặc định: sắp xếp theo "ngayTao" giảm dần (DESC)
        Sort sort = SortUtils.createSort(
            sortBy, 
            sortDirection, 
            "ngayTao", 
            Sort.Direction.DESC
        );

        // Đảm bảo page và size hợp lệ
        page = Math.max(0, page);
        size = Math.min(size, 100); 
        size = Math.max(1, size);
        
        Pageable pageable = PageRequest.of(page, size, sort);

        // Gọi Repository và chuyển đổi sang DTO
        Page<LoaiDichVu> loaiDichVuPage = loaiDichVuRepository.findAll(pageable);

        List<LoaiDichVuResponseDTO> dtos = loaiDichVuPage.getContent().stream()
                .map(LoaiDichVuResponseDTO::new) 
                .collect(Collectors.toList());
        
        // Trả về PageResponseDTO
        return new PageResponseDTO<>(
            dtos,
            loaiDichVuPage.getNumber(),
            loaiDichVuPage.getSize(),
            loaiDichVuPage.getTotalElements(),
            loaiDichVuPage.getTotalPages(),
            loaiDichVuPage.isLast()
        );
    }


     // TÌM KIẾM & PHÂN TRANG & SẮP XẾP
    public PageResponseDTO<LoaiDichVuResponseDTO> searchServiceTypes(
            int page, int size, String sortBy, String sortDirection,
            String tenLoai, String trangThai) {
        
        // 1. Xây dựng Specification (logic tìm kiếm)
        Specification<LoaiDichVu> spec = Specification.where(null); 
        
        // Tìm kiếm theo tên (LIKE)
        spec = spec.and(JpaSpecificationUtil.attributeContains("tenLoai", tenLoai));
        
        // Tìm kiếm theo trạng thái (EQUAL)
        spec = spec.and(JpaSpecificationUtil.attributeEquals("trangThai", trangThai));

        // 2. Xây dựng Pageable (Phân trang và Sắp xếp)
        // Lưu ý: Có thể đặt mặc định sắp xếp khác nếu bạn muốn tìm kiếm có mặc định riêng.
        Sort sort = SortUtils.createSort(sortBy, sortDirection, "ngayTao", Sort.Direction.DESC);
        page = Math.max(0, page);
        size = Math.min(size, 100); 
        size = Math.max(1, size);
        
        Pageable pageable = PageRequest.of(page, size, sort);

        // 3. Gọi Repository để tìm kiếm (sử dụng Specification và Pageable)
        Page<LoaiDichVu> loaiDichVuPage = loaiDichVuRepository.findAll(spec, pageable);

        // 4. Chuyển đổi sang DTO và trả về
        List<LoaiDichVuResponseDTO> dtos = loaiDichVuPage.getContent().stream()
                .map(LoaiDichVuResponseDTO::new) 
                .collect(Collectors.toList());
        
        return new PageResponseDTO<>(
            dtos, loaiDichVuPage.getNumber(), loaiDichVuPage.getSize(), 
            loaiDichVuPage.getTotalElements(), loaiDichVuPage.getTotalPages(), 
            loaiDichVuPage.isLast()
        );
    }


    //HÀM CẬP NHẬT THÔNG TIN lOẠI DỊCH VỤ
    /**
     * Cập nhật thông tin loại dịch vụ, chỉ ghi đè các trường được gửi (Partial Update).
     * @param maLoai Mã loại dịch vụ cần sửa.
     * @param updateDTO Dữ liệu cập nhật.
     * @return LoaiDichVu đã được cập nhật.
     * @throws ResourceNotFoundException Nếu không tìm thấy loại dịch vụ.
     * @throws ResourceAlreadyExistsException Nếu tên mới bị trùng với loại dịch vụ khác.
     * @throws IllegalArgumentException Nếu trạng thái được gửi nhưng không hợp lệ.
     */
    @Transactional
    public LoaiDichVu updateServiceType(Integer maLoai, LoaiDichVuUpdateDTO updateDTO) {
        
        // 1. Tìm loại dịch vụ theo ID
        LoaiDichVu existingType = loaiDichVuRepository.findById(maLoai)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Loại Dịch Vụ với Mã: " + maLoai));

        // --- Cập nhật Tên Loại (Chỉ khi được gửi) ---
        String newTenLoai = updateDTO.getTenLoai();
        if (newTenLoai != null && !newTenLoai.trim().isEmpty()) {
            
            // Kiểm tra tên loại dịch vụ mới có bị trùng với loại khác (trừ chính nó)
            loaiDichVuRepository.findByTenLoai(newTenLoai).ifPresent(type -> {
                if (!type.getMaLoai().equals(maLoai)) {
                    throw new ResourceAlreadyExistsException("Tên loại dịch vụ đã tồn tại: " + newTenLoai);
                }
            });
            
            existingType.setTenLoai(newTenLoai);
        }
        
        // --- Cập nhật Trạng Thái (Chỉ khi được gửi) ---
        String newTrangThai = updateDTO.getTrangThai();
        if (newTrangThai != null && !newTrangThai.trim().isEmpty()) {
            
            // Logic validation trạng thái (Hoạt động hoặc Đã xóa)
            if (!("Hoạt động".equals(newTrangThai) || "Đã xóa".equals(newTrangThai))) {
                throw new IllegalArgumentException("Trạng thái không hợp lệ. Chỉ chấp nhận 'Hoạt động' hoặc 'Đã xóa'.");
            }
            
            existingType.setTrangThai(newTrangThai);
        }

        // 3. Lưu và trả về
        return loaiDichVuRepository.save(existingType);
    }


    //HÀM XÓA MỀM DỮ LIỆU
    @Transactional
    public LoaiDichVu softDeleteServiceType(Integer maLoai) {
        
        //  Giữ logic trong Service và gọi lại từ hàm tiện ích
        
        // Hoặc cách đơn giản: giữ nguyên code cũ (đã được viết trước đó)
        LoaiDichVu entity = loaiDichVuRepository.findById(maLoai)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Loại Dịch Vụ với Mã: " + maLoai));

        if ("Đã xóa".equals(entity.getTrangThai())) {
            throw new IllegalStateException("Loại dịch vụ này đã ở trạng thái 'Đã xóa' và không thể xóa tiếp.");
        }
        
        entity.setTrangThai("Đã xóa");
        return loaiDichVuRepository.save(entity);
    }
    public Map<String, Long> thongKeLoaiDichVu() {
        Map<String, Long> result = new HashMap<>();

        long tongSoLoai = loaiDichVuRepository.count();
        long soLoaiHoatDong = loaiDichVuRepository.countByTrangThai("Hoạt động");

        // Tính ngày bắt đầu của 30 ngày gần nhất
        LocalDateTime thangTruoc = LocalDateTime.now().minusDays(30);
        long soLoaiMoi = loaiDichVuRepository.countNewInLastMonth(thangTruoc);

        result.put("tongSoLoaiDichVu", tongSoLoai);
        result.put("soLoaiDichVuHoatDong", soLoaiHoatDong);
        result.put("soLoaiDichVuMoiThangQua", soLoaiMoi);

        return result;
    }
}