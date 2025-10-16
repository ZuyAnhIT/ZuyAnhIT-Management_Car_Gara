package com.example.gara_management.service;

import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.DichVuDTO.DichVuCreateDTO;
import com.example.gara_management.dto.DichVuDTO.DichVuResponseDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
@Service
public class DichVuService {

    private final DichVuRepository dichVuRepository;
    private final LoaiDichVuRepository loaiDichVuRepository; // Cần dùng Repository của LoaiDichVu

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


}