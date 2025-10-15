package com.example.gara_management.controller;

import com.example.gara_management.dto.LoaiDichVuCreateDTO;
import com.example.gara_management.model.LoaiDichVu;
import com.example.gara_management.service.LoaiDichVuService;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid; // Thêm import này
@Tag(name = "Quản lý Loại Dịch Vụ", description = "API thêm, sửa, xóa, xem loại dịch vụ trong hệ thống gara")
@RestController
@RequestMapping("/api/loaidichvu") // Đặt tên endpoint rõ ràng
public class LoaiDichVuController {

    private final LoaiDichVuService loaiDichVuService;

    // Dependency Injection qua Constructor
    public LoaiDichVuController(LoaiDichVuService loaiDichVuService) {
        this.loaiDichVuService = loaiDichVuService;
    }

    /**
     * Endpoint POST để thêm loại dịch vụ mới.
     * @param createDTO Dữ liệu đầu vào từ body request, được validated.
     * @return ResponseEntity chứa đối tượng LoaiDichVu đã tạo hoặc thông báo lỗi.
     */
    
     // API thêm loại dịch vụ
    @PostMapping("/them")
    public ResponseEntity<?> createLoaiDichVu(@Valid @RequestBody LoaiDichVuCreateDTO createDTO) {
        try {
            // Gọi Service để thực hiện nghiệp vụ
            LoaiDichVu newLoaiDichVu = loaiDichVuService.themLoaiDichVu(createDTO);
            
            // Trả về đối tượng vừa tạo với HTTP Status 201 Created
            return new ResponseEntity<>(newLoaiDichVu, HttpStatus.CREATED); 
            
        } catch (ResourceAlreadyExistsException e) {
            // Xử lý lỗi nghiệp vụ (ví dụ: trùng tên) với HTTP Status 409 Conflict
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            
        } catch (Exception e) {
            // Xử lý các lỗi khác (ví dụ: lỗi DB, server) với HTTP Status 500 Internal Server Error
            // Nên ghi log lỗi chi tiết ở đây
            return new ResponseEntity<>("Lỗi hệ thống không xác định: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //API hiển thị danh sách loại dịch vụ
    
}