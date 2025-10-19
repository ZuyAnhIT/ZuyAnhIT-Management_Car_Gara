package com.example.gara_management.controller;


import com.example.gara_management.dto.*;
import com.example.gara_management.dto.HoaDonDTO.HoaDonDTO;
import com.example.gara_management.dto.PhieuSuaChuaDTO.CreatePhieuSuaChuaRequest;
import com.example.gara_management.dto.PhieuSuaChuaDTO.PhieuSuaChuaDTO;
import com.example.gara_management.exception.ResourceNotFoundException;
import com.example.gara_management.service.PhieuSuaChuaService;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.gara_management.service.HoaDonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/phieusuachua")
@Tag(name = "Quản lý Phiếu Sửa Chữa", description = "API thêm, sửa, tìm kiếm hiển thị trong quản lý phiếu sữa chữa trong hệ thống gara")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PhieuSuaChuaController {

    private final PhieuSuaChuaService phieuSuaChuaService;
    private final HoaDonService hoaDonService;

    // --- 1. TẠO PHIẾU SỬA CHỮA MỚI ---
    @PostMapping("them")
    public ResponseEntity<ApiResponse<PhieuSuaChuaDTO>> createPhieuSuaChua(
            @Valid @RequestBody CreatePhieuSuaChuaRequest request) {
        try {
            PhieuSuaChuaDTO result = phieuSuaChuaService.createPhieuSuaChua(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Tạo phiếu sửa chữa thành công", result));
        } catch (ResourceNotFoundException e) {
            // Lỗi nghiệp vụ (Xe, Thợ, Dịch vụ không tồn tại)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Lỗi tạo phiếu: " + e.getMessage()));
        } catch (Exception e) {
            // Các lỗi khác (Validation, DB, logic...)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Lỗi không xác định khi tạo phiếu: " + e.getMessage()));
        }
    }

    // ----------------------------------------------------------------------------------
    // --- 2. HIỂN THỊ DANH SÁCH (CÓ PHÂN TRANG & SẮP XẾP) - THAY THẾ getAllPhieuSuaChua cũ ---
    // ----------------------------------------------------------------------------------
    @GetMapping("HienThiDanhSach")
    public ResponseEntity<ApiResponse<PageResponseDTO<PhieuSuaChuaDTO>>> getAllPhieuSuaChua(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "ngayLap") String sortBy, 
            @RequestParam(required = false, defaultValue = "desc") String sortDirection) {
        
        try {
            PageResponseDTO<PhieuSuaChuaDTO> result = 
                phieuSuaChuaService.getAllPhieuSuaChua(page, size, sortBy, sortDirection);
            
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách phiếu thành công", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống khi lấy danh sách: " + e.getMessage()));
        }
    }
    
    // ----------------------------------------------------------------------------------
    // --- 3. TÌM KIẾM/LỌC (API RIÊNG BIỆT) 
    // ----------------------------------------------------------------------------------
    @GetMapping("/timKiem")
    public ResponseEntity<ApiResponse<PageResponseDTO<PhieuSuaChuaDTO>>> searchPhieuSuaChua(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "ngayLap") String sortBy, 
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String trangThai, 
            @RequestParam(required = false) String bienSo, 
            @RequestParam(required = false) Integer maTho) {
        
        try {
            PageResponseDTO<PhieuSuaChuaDTO> result = 
                phieuSuaChuaService.searchPhieuSuaChua(
                    page, size, sortBy, sortDirection, 
                    trangThai, bienSo
                );
            return ResponseEntity.ok(ApiResponse.success("Tìm kiếm phiếu thành công", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Lỗi tìm kiếm: " + e.getMessage()));
        }
    }
    
    // --- 5. CẬP NHẬT TRẠNG THÁI PHIẾU SỬA CHỮA ---
    @PatchMapping("/{maPhieu}/capNhatTrangThai")
    public ResponseEntity<ApiResponse<PhieuSuaChuaDTO>> updateTrangThai(
            @PathVariable Integer maPhieu,
            @RequestParam String trangThai) {
        try {
            PhieuSuaChuaDTO result = phieuSuaChuaService.updateTrangThai(maPhieu, trangThai);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công", result));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Lỗi: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // Có thể là lỗi validation trạng thái không hợp lệ
                    .body(ApiResponse.error("Lỗi cập nhật trạng thái: " + e.getMessage()));
        }
    }

    // --- 6. LẤY HÓA ĐƠN CỦA PHIẾU SỬA CHỮA ---
    @GetMapping("/{maPhieu}/layHoaDon")
    public ResponseEntity<ApiResponse<HoaDonDTO>> getHoaDonByMaPhieu(@PathVariable Integer maPhieu) {
        try {
            HoaDonDTO result = hoaDonService.getHoaDonByMaPhieu(maPhieu);
            return ResponseEntity.ok(ApiResponse.success("Lấy hóa đơn thành công", result));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Lỗi: " + e.getMessage()));
        } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống: " + e.getMessage()));
        }
    }
}