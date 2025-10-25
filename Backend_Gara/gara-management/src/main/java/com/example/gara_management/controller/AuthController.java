package com.example.gara_management.controller;

import com.example.gara_management.dto.ApiResponse;
import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.auth.*;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;
import com.example.gara_management.model.TaiKhoan;
import com.example.gara_management.service.AuthService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Tag(name = "Auth", description = "API đăng nhập, đăng ký.....")

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;

    // Đăng nhập
    @PostMapping("/dangNhap")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Tên đăng nhập hoặc mật khẩu không đúng"));
        }
    }
    
    // Đăng ký
    @PostMapping("/dangKy")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Đăng ký tài khoản thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Lỗi: " + e.getMessage()));
        }
    }
    
    // Đổi mật khẩu
    @PutMapping("/doiMatKhau")
    public ResponseEntity<ApiResponse<String>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            String username = authentication.getName();
            authService.changePassword(username, request);
            return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Lỗi: " + e.getMessage()));
        }
    }
    // ====================== THỐNG KÊ TÀI KHOẢN ======================
    @GetMapping("/thongKeTaiKhoan")
    public ResponseEntity<Map<String, Long>> thongKeTaiKhoan() {
        Map<String, Long> result = authService.thongKeTaiKhoan();
        return ResponseEntity.ok(result);
    }
    // ================== HIỂN THỊ DANH SÁCH ==================
    @GetMapping("/hienThiDanhSach")
    public ResponseEntity<PageResponseDTO<TaiKhoanResponseDTO>> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection
    ) {
        return ResponseEntity.ok(authService.getAllPaged(page, size, sortBy, sortDirection));
    }

    // ================== TÌM KIẾM ==================
    @GetMapping("/timKiem")
    public ResponseEntity<PageResponseDTO<TaiKhoanResponseDTO>> searchTaiKhoan(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "ngayTao") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String tenDangNhap,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String vaiTro,
            @RequestParam(required = false) String trangThai
    ) {
        try {
            PageResponseDTO<TaiKhoanResponseDTO> result = authService.searchTaiKhoan(
                    page, size, sortBy, sortDirection, tenDangNhap, email, vaiTro, trangThai
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            PageResponseDTO<TaiKhoanResponseDTO> emptyPage = new PageResponseDTO<>(
                    List.of(), page, size, 0L, 0, true
            );
            return new ResponseEntity<>(emptyPage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ================== CẬP NHẬT ==================
    @PutMapping("/capNhat/{id}")
    public ResponseEntity<?> updateTaiKhoan(
            @PathVariable Integer id,
            @Valid @RequestBody TaiKhoanUpdateDTO updateDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                    .orElse("Dữ liệu không hợp lệ");
            return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
        }

        try {
            TaiKhoan updated = authService.updateTaiKhoan(id, updateDTO);
            TaiKhoanResponseDTO responseDTO = new TaiKhoanResponseDTO(updated);
            return ResponseEntity.ok(responseDTO);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ResourceAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi hệ thống: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
