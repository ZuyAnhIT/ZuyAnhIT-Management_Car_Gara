package com.example.gara_management.service;

import com.example.gara_management.dto.LoaiDichVuCreateDTO;
import com.example.gara_management.model.LoaiDichVu;
import com.example.gara_management.repository.LoaiDichVuRepository;
import com.example.gara_management.exception.ResourceAlreadyExistsException; // Sử dụng Exception tùy chỉnh (sẽ tạo ở phần sau)
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LoaiDichVuService {

    private final LoaiDichVuRepository loaiDichVuRepository;

    // Dependency Injection qua Constructor
    public LoaiDichVuService(LoaiDichVuRepository loaiDichVuRepository) {
        this.loaiDichVuRepository = loaiDichVuRepository;
    }

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
}