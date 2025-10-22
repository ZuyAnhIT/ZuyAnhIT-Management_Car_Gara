package com.example.gara_management.controller;

import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.KhachHangDTO.KhachHangCreateDTO;
import com.example.gara_management.dto.KhachHangDTO.KhachHangResponseDTO;
import com.example.gara_management.dto.KhachHangDTO.KhachHangUpdateDTO;
import com.example.gara_management.model.KhachHang;
import com.example.gara_management.service.KhachHangService;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Quản lý Khách Hàng", description = "API thêm, sửa, xóa, xem danh sách khách hàng trong hệ thống gara")
@RestController
@RequestMapping("/api/khachhang")
public class KhachHangController {

    private final KhachHangService khachHangService;

    public KhachHangController(KhachHangService khachHangService) {
        this.khachHangService = khachHangService;
    }

    // ================================================================
    // 🧩 1️⃣ API THÊM KHÁCH HÀNG
    // ================================================================
    /**
     * Endpoint POST để thêm khách hàng mới.
     * URL: POST /api/khachhang
     */
    @PostMapping("them")
    public ResponseEntity<?> createKhachHang(@Valid @RequestBody KhachHangCreateDTO createDTO) {
        try {
            // 1. Gọi Service để thực hiện logic nghiệp vụ
            KhachHang newKhachHang = khachHangService.themKhachHang(createDTO);
            
            // 2. Chuyển Entity sang DTO để trả về
            KhachHangResponseDTO responseDTO = new KhachHangResponseDTO(newKhachHang);
            
            // 3. Trả về đối tượng vừa tạo với HTTP Status 201 Created
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED); 
            
        } catch (ResourceAlreadyExistsException e) {
            // Xử lý lỗi nghiệp vụ: trùng SĐT/Email (409 Conflict)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            
        } catch (Exception e) {
            // Xử lý các lỗi khác (500 Internal Server Error)
            // Cần log lỗi chi tiết ở đây
            return new ResponseEntity<>("Lỗi hệ thống khi thêm khách hàng.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ----------------------------------------------------------------------
    // --- API 1: CHỈ HIỂN THỊ DANH SÁCH & SẮP XẾP (Giữ nguyên API cũ) ---
    // ----------------------------------------------------------------------
    @GetMapping("hienThiDanhSach")
    public ResponseEntity<PageResponseDTO<KhachHangResponseDTO>> getAllKhachHang(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "maKhachHang") String sortBy, 
            @RequestParam(required = false, defaultValue = "desc") String sortDirection) { 
        
        PageResponseDTO<KhachHangResponseDTO> responseDTO = 
                khachHangService.getAllKhachHang(page, size, sortBy, sortDirection);
        
        return ResponseEntity.ok(responseDTO);
    }

    // ----------------------------------------------------------------------
    // --- API 2: TÌM KIẾM, LỌC & PHÂN TRANG (Cập nhật tham số) ---
    // ----------------------------------------------------------------------
    /**
     * Endpoint GET riêng để tìm kiếm/lọc khách hàng.
     * URL ví dụ: /api/khachhang/search?tenKhachHang=Nguyễn Văn A&soDienThoai=0987&trangThai=Hoạt động
     */
    @GetMapping("/timKiem") 
    public ResponseEntity<PageResponseDTO<KhachHangResponseDTO>> searchKhachHang(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "maKhachHang") String sortBy, 
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            
            // Cập nhật tham số tìm kiếm
            @RequestParam(required = false) String tenKhachHang, 
            @RequestParam(required = false) String soDienThoai, 
            @RequestParam(required = false) String email, 
            @RequestParam(required = false) String trangThai, 
            @RequestParam(required = false) String loaiKhach) { 
        
        PageResponseDTO<KhachHangResponseDTO> responseDTO = 
                khachHangService.searchKhachHang(
                    page, size, sortBy, sortDirection, 
                    tenKhachHang, soDienThoai, email, trangThai, loaiKhach
                );
        
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Endpoint PUT để sửa thông tin khách hàng (Partial Update).
     * URL: PUT /api/khachhang/{maKhachHang}
     */
    @PutMapping("/{maKhachHang}")
    public ResponseEntity<?> updateKhachHang(
            @PathVariable Integer maKhachHang, 
            @Valid @RequestBody KhachHangUpdateDTO updateDTO) {
        try {
            // 1. Gọi Service để cập nhật
            KhachHang updatedKH = khachHangService.updateKhachHang(maKhachHang, updateDTO);
            
            // 2. Chuyển Entity sang DTO để trả về
            KhachHangResponseDTO responseDTO = new KhachHangResponseDTO(updatedKH);
            
            return ResponseEntity.ok(responseDTO);
            
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404
            
        } catch (ResourceAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409
            
        } catch (Exception e) {
            // Bao gồm lỗi Validation (kích thước, định dạng)
            return new ResponseEntity<>("Lỗi hệ thống khi cập nhật khách hàng: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    // ================================================================
    // 🧩 5️⃣ API XÓA MỀM KHÁCH HÀNG
    // ================================================================
    /**
     * Endpoint DELETE để thực hiện xóa mềm (Soft Delete) khách hàng.
     * @param maKhachHang Mã khách hàng cần xóa.
     */
    @DeleteMapping("/{maKhachHang}")
    public ResponseEntity<?> softDeleteCustomer(@PathVariable Integer maKhachHang) {
        try {
            KhachHang deletedCustomer = khachHangService.softDeleteCustomer(maKhachHang);
            return ResponseEntity.ok(new KhachHangResponseDTO(deletedCustomer));

        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404

        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409

        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi hệ thống khi xóa mềm khách hàng: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
