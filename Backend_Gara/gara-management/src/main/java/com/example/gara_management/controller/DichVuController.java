package com.example.gara_management.controller;



import com.example.gara_management.dto.DichVuDTO.DichVuCreateDTO;
import com.example.gara_management.model.DichVu;
import com.example.gara_management.service.DichVuService;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Tag(name = "Quản lý Dịch Vụ", description = "API thêm, sửa, xóa, xem dịch vụ trong hệ thống gara")
@RestController
@RequestMapping("/api/dichvu") // Endpoint cho Dịch Vụ
public class DichVuController {

    private final DichVuService dichVuService;

    public DichVuController(DichVuService dichVuService) {
        this.dichVuService = dichVuService;
    }


    // API THÊM DỊCH VỤ
    /**
     * Endpoint POST để thêm dịch vụ mới.
     */
    @PostMapping("them")
    public ResponseEntity<?> createService(@Valid @RequestBody DichVuCreateDTO createDTO) {
        try {
            DichVu newService = dichVuService.addService(createDTO);
            
            // Trả về đối tượng vừa tạo với HTTP Status 201 Created
            return new ResponseEntity<>(newService, HttpStatus.CREATED); 
            
        } catch (ResourceNotFoundException e) {
            // Lỗi nghiệp vụ: Không tìm thấy Loại Dịch Vụ (404 Not Found)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            
        } catch (ResourceAlreadyExistsException e) {
            // Lỗi nghiệp vụ: Trùng tên Dịch Vụ (409 Conflict)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            
        } catch (Exception e) {
            // Lỗi khác (500 Internal Server Error)
            return new ResponseEntity<>("Lỗi hệ thống khi thêm dịch vụ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
