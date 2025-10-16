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
     * URL: /api/khachhang/them
     */
    @PostMapping("/them")
    public ResponseEntity<?> createCustomer(@Valid @RequestBody KhachHangCreateDTO createDTO) {
        try {
            KhachHang newCustomer = khachHangService.addCustomer(createDTO);
            return new ResponseEntity<>(new KhachHangResponseDTO(newCustomer), HttpStatus.CREATED);

        } catch (ResourceAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409 Conflict

        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi hệ thống khi thêm khách hàng: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ================================================================
    // 🧩 2️⃣ API HIỂN THỊ DANH SÁCH KHÁCH HÀNG (PHÂN TRANG + SORT)
    // ================================================================
    /**
     * Endpoint GET để hiển thị danh sách khách hàng
     * URL: /api/khachhang/hienThiDanhSach?page=0&size=10&sortBy=tenKhachHang&sortDirection=asc
     */
    @GetMapping("/hienThiDanhSach")
    public ResponseEntity<PageResponseDTO<KhachHangResponseDTO>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {

        PageResponseDTO<KhachHangResponseDTO> response =
                khachHangService.getAllCustomers(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    // ================================================================
    // 🧩 3️⃣ API TÌM KIẾM KHÁCH HÀNG (SEARCH)
    // ================================================================
    /**
     * Endpoint GET riêng để tìm kiếm khách hàng theo các tiêu chí.
     * URL: /api/khachhang/timKiem?tenKhachHang=An&soDienThoai=0901&loaiKhach=Doanh nghiệp
     */
    @GetMapping("/timKiem")
    public ResponseEntity<PageResponseDTO<KhachHangResponseDTO>> searchCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String tenKhachHang,
            @RequestParam(required = false) String soDienThoai,
            @RequestParam(required = false) String loaiKhach) {

        PageResponseDTO<KhachHangResponseDTO> response =
                khachHangService.searchCustomers(page, size, sortBy, sortDirection, tenKhachHang, soDienThoai, loaiKhach);

        return ResponseEntity.ok(response);
    }

    // ================================================================
    // 🧩 4️⃣ API CẬP NHẬT THÔNG TIN KHÁCH HÀNG
    // ================================================================
    /**
     * Endpoint PUT để cập nhật thông tin khách hàng.
     * URL: /api/khachhang/{maKhachHang}
     */
    @PutMapping("/{maKhachHang}")
    public ResponseEntity<?> updateCustomer(
            @PathVariable Integer maKhachHang,
            @Valid @RequestBody KhachHangUpdateDTO updateDTO) {
        try {
            KhachHang updatedCustomer = khachHangService.updateCustomer(maKhachHang, updateDTO);
            return ResponseEntity.ok(new KhachHangResponseDTO(updatedCustomer));

        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404

        } catch (ResourceAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400

        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi hệ thống khi cập nhật khách hàng: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
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
