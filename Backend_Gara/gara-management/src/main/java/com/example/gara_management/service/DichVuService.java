package com.example.gara_management.service;

import com.example.gara_management.dto.DichVuDTO.DichVuCreateDTO;
import com.example.gara_management.model.DichVu;
import com.example.gara_management.model.LoaiDichVu;
import com.example.gara_management.repository.DichVuRepository;
import com.example.gara_management.repository.LoaiDichVuRepository;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DichVuService {

    private final DichVuRepository dichVuRepository;
    private final LoaiDichVuRepository loaiDichVuRepository; // Cần dùng Repository của LoaiDichVu

    public DichVuService(DichVuRepository dichVuRepository, LoaiDichVuRepository loaiDichVuRepository) {
        this.dichVuRepository = dichVuRepository;
        this.loaiDichVuRepository = loaiDichVuRepository;
    }


    //HÀM THÊM DỊCH VỤ
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
}