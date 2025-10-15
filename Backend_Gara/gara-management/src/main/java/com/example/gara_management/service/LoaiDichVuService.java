package com.example.gara_management.service;

import com.example.gara_management.model.LoaiDichVu;
import com.example.gara_management.repository.LoaiDichVuRepository;
import com.example.gara_management.util.JpaSpecificationUtil;
import com.example.gara_management.util.SortUtils;
import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.LoaiDichVuDTO.LoaiDichVuCreateDTO;
import com.example.gara_management.dto.LoaiDichVuDTO.LoaiDichVuResponseDTO;
import com.example.gara_management.exception.ResourceAlreadyExistsException; 
import org.springframework.transaction.annotation.Transactional;

// Hỗ trợ phân trang
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
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

}