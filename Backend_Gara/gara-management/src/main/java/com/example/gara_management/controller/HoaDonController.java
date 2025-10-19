package com.example.gara_management.controller;


import com.example.gara_management.dto.ApiResponse;
import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.HoaDonDTO.*;
import com.example.gara_management.exception.ResourceNotFoundException;
import com.example.gara_management.service.HoaDonService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hoadon")
@Tag(name = "Quản lý Hóa Đơn", description = "API thêm, sửa, tìm kiếm hiển thị trong quản lý hóa đơn trong hệ thống gara")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HoaDonController {
    
    private final HoaDonService hoaDonService;
    
    // --- 1. HIỂN THỊ DANH SÁCH (CÓ PHÂN TRANG & SẮP XẾP) ---
    @GetMapping("hienThiDanhSach")
    public ResponseEntity<ApiResponse<PageResponseDTO<HoaDonDTO>>> getAllHoaDon(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "ngayLapHoaDon") String sortBy, 
            @RequestParam(required = false, defaultValue = "desc") String sortDirection) {
        
        try {
            PageResponseDTO<HoaDonDTO> result = 
                hoaDonService.getAllHoaDon(page, size, sortBy, sortDirection);
            
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách hóa đơn thành công", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống khi lấy danh sách: " + e.getMessage()));
        }
    }

    // --- 2. TÌM KIẾM/LỌC (CÓ PHÂN TRANG & SẮP XẾP) ---
    @GetMapping("/timKiem")
    public ResponseEntity<ApiResponse<PageResponseDTO<HoaDonDTO>>> searchHoaDon(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "ngayLapHoaDon") String sortBy, 
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) Integer maPhieu,
            @RequestParam(required = false) BigDecimal tongTienMin,
            @RequestParam(required = false) BigDecimal tongTienMax,
            @RequestParam(required = false) String trangThai) {
        
        try {
            PageResponseDTO<HoaDonDTO> result = 
                hoaDonService.searchHoaDon(
                    page, size, sortBy, sortDirection, 
                    maPhieu, tongTienMin, tongTienMax, trangThai
                );
            return ResponseEntity.ok(ApiResponse.success("Tìm kiếm hóa đơn thành công", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Lỗi tìm kiếm: " + e.getMessage()));
        }
    }
    
    // --- 3A. CẬP NHẬT TRẠNG THÁI (THANH TOÁN/HỦY) ---
    @PatchMapping("/{maHoaDon}/trangThai")
    public ResponseEntity<ApiResponse<HoaDonDTO>> updateTrangThaiHoaDon(
            @PathVariable Integer maHoaDon,
            @RequestParam String trangThai) { // Không còn kieuThanhToan ở đây
        try {
            HoaDonDTO result = hoaDonService.capNhatTrangThaiHoaDon(maHoaDon, trangThai);
            
            return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái hóa đơn thành công", result));
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Lỗi: " + e.getMessage()));
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Lỗi nghiệp vụ (ví dụ: đã thanh toán, không cho phép hủy, trạng thái không hợp lệ)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Lỗi nghiệp vụ: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống: " + e.getMessage()));
        }
    }
    
    // --- 3B. CẬP NHẬT KIỂU THANH TOÁN (TÁCH BIỆT) ---
    @PatchMapping("/{maHoaDon}/kieuThanhToan")
    public ResponseEntity<ApiResponse<HoaDonDTO>> updateKieuThanhToan(
            @PathVariable Integer maHoaDon,
            @RequestParam String kieuThanhToan) {
        try {
            HoaDonDTO result = hoaDonService.capNhatKieuThanhToan(maHoaDon, kieuThanhToan);
            
            return ResponseEntity.ok(ApiResponse.success("Cập nhật kiểu thanh toán thành công", result));
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Lỗi: " + e.getMessage()));
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Lỗi nghiệp vụ (chỉ sửa khi chưa thanh toán, kiểu không hợp lệ)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Lỗi nghiệp vụ: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống: " + e.getMessage()));
        }
    }
    
}