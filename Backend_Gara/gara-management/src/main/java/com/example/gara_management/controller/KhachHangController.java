package com.example.gara_management.controller;

import com.example.gara_management.dto.KhachHangDTO.KhachHangCreateDTO;
import com.example.gara_management.dto.KhachHangDTO.KhachHangResponseDTO;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.model.KhachHang;
import com.example.gara_management.service.KhachHangService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Quản lý Khách Hàng", description = "API thêm, sửa, xóa, xem thông tin khách hàng")
@RestController
@RequestMapping("/api/khachhang")
public class KhachHangController {

    private final KhachHangService khachHangService;

    public KhachHangController(KhachHangService khachHangService) {
        this.khachHangService = khachHangService;
    }

    /**
     * 🧠 API: Thêm mới khách hàng
     * URL: POST /api/khachhang
     * Request body: KhachHangCreateDTO
     * Response: KhachHangResponseDTO
     */
    @PostMapping("them")
    public ResponseEntity<?> themKhachHang(@Valid @RequestBody KhachHangCreateDTO dto) {
        try {
            KhachHang created = khachHangService.themKhachHang(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new KhachHangResponseDTO(created));

        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Loi he thong khi them khach hang: " + e.getMessage());
        }
    }
}
