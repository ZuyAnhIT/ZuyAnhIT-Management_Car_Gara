package com.example.gara_management.service;

import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.auth.*;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;
import com.example.gara_management.model.TaiKhoan;
import com.example.gara_management.repository.TaiKhoanRepository;
import com.example.gara_management.security.JwtTokenProvider;
import com.example.gara_management.util.JpaSpecificationUtil;
import com.example.gara_management.util.SortUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import java.util.stream.Collectors;

import org.springframework.data.domain.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    
    // Đăng nhập
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Xác thực username và password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getTenDangNhap(),
                        request.getMatKhau()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Tạo JWT token
        String jwt = tokenProvider.generateToken(authentication);
        
        // Lấy thông tin tài khoản
        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(request.getTenDangNhap())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        
        return AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .maTaiKhoan(taiKhoan.getMaTaiKhoan())
                .tenDangNhap(taiKhoan.getTenDangNhap())
                .email(taiKhoan.getEmail())
                .vaiTro(taiKhoan.getVaiTro())
                .trangThai(taiKhoan.getTrangThai())
                .build();
    }
    
    // Đăng ký tài khoản mới
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Kiểm tra tên đăng nhập đã tồn tại
        if (taiKhoanRepository.existsByTenDangNhap(request.getTenDangNhap())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        
        // Kiểm tra email đã tồn tại
        if (taiKhoanRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }
        
        // Tạo tài khoản mới với mật khẩu đã mã hóa
        TaiKhoan taiKhoan = TaiKhoan.builder()
                .tenDangNhap(request.getTenDangNhap())
                .matKhau(passwordEncoder.encode(request.getMatKhau())) // Mã hóa mật khẩu
                .email(request.getEmail())
                .vaiTro(request.getVaiTro())
                .trangThai("Hoạt động")
                .ngayTao(LocalDateTime.now())
                .build();
        
        taiKhoanRepository.save(taiKhoan);
        
        // Tạo JWT token
        String jwt = tokenProvider.generateTokenFromUsername(taiKhoan.getTenDangNhap());
        
        return AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .maTaiKhoan(taiKhoan.getMaTaiKhoan())
                .tenDangNhap(taiKhoan.getTenDangNhap())
                .email(taiKhoan.getEmail())
                .vaiTro(taiKhoan.getVaiTro())
                .trangThai(taiKhoan.getTrangThai())
                .build();
    }
    
    // Đổi mật khẩu
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        
        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getMatKhauCu(), taiKhoan.getMatKhau())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }
        
        // Cập nhật mật khẩu mới
        taiKhoan.setMatKhau(passwordEncoder.encode(request.getMatKhauMoi()));
        taiKhoanRepository.save(taiKhoan);
    }
    
    // Lấy thông tin tài khoản hiện tại
    public TaiKhoan getCurrentUser(String username) {
        return taiKhoanRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
    }
    // ====================== THỐNG KÊ TÀI KHOẢN ======================
    public Map<String, Long> thongKeTaiKhoan() {
        long tongSoTaiKhoan = taiKhoanRepository.count();
        long soTaiKhoanHoatDong = taiKhoanRepository.countByTrangThai("Hoạt động");
        long soTaiKhoanNhanVien = taiKhoanRepository.countByVaiTro("Nhân viên");

        Map<String, Long> result = new HashMap<>();
        result.put("tongSoTaiKhoan", tongSoTaiKhoan);
        result.put("soTaiKhoanHoatDong", soTaiKhoanHoatDong);
        result.put("soTaiKhoanNhanVien", soTaiKhoanNhanVien);
        return result;
    }

     // ================== HIỂN THỊ DANH SÁCH ==================
    public PageResponseDTO<TaiKhoanResponseDTO> getAllPaged(int page, int size, String sortBy, String sortDirection) {
        Sort sort = SortUtils.createSort(sortBy, sortDirection, "ngayTao", Sort.Direction.DESC);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
        Page<TaiKhoan> taiKhoanPage = taiKhoanRepository.findAll(pageable);

        List<TaiKhoanResponseDTO> dtos = taiKhoanPage.getContent()
                .stream().map(TaiKhoanResponseDTO::new)
                .collect(Collectors.toList());

        return new PageResponseDTO<>(dtos, taiKhoanPage.getNumber(), taiKhoanPage.getSize(),
                taiKhoanPage.getTotalElements(), taiKhoanPage.getTotalPages(), taiKhoanPage.isLast());
    }

    // ================== TÌM KIẾM ==================
    public PageResponseDTO<TaiKhoanResponseDTO> searchTaiKhoan(
            int page, int size, String sortBy, String sortDirection,
            String tenDangNhap, String email, String vaiTro, String trangThai) {

        Specification<TaiKhoan> spec = Specification.where((Specification<TaiKhoan>) null)
                .and(JpaSpecificationUtil.<TaiKhoan>attributeContains("tenDangNhap", tenDangNhap))
                .and(JpaSpecificationUtil.<TaiKhoan>attributeContains("email", email))
                .and(JpaSpecificationUtil.<TaiKhoan>attributeContains("vaiTro", vaiTro))
                .and(JpaSpecificationUtil.<TaiKhoan>attributeEquals("trangThai", trangThai));

        Sort sort = SortUtils.createSort(sortBy, sortDirection, "ngayTao", Sort.Direction.DESC);
        page = Math.max(0, page);
        size = Math.max(1, size);

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TaiKhoan> taiKhoanPage = taiKhoanRepository.findAll(spec, pageable);

        List<TaiKhoanResponseDTO> dtos = taiKhoanPage.getContent()
                .stream().map(TaiKhoanResponseDTO::new)
                .collect(Collectors.toList());

        return new PageResponseDTO<>(dtos, taiKhoanPage.getNumber(), taiKhoanPage.getSize(),
                taiKhoanPage.getTotalElements(), taiKhoanPage.getTotalPages(), taiKhoanPage.isLast());
    }

    // ================== CẬP NHẬT ==================
    @Transactional
    public TaiKhoan updateTaiKhoan(Integer id, TaiKhoanUpdateDTO dto) {
        TaiKhoan taiKhoan = taiKhoanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với id: " + id));

        // Kiểm tra tồn tại TenDangNhap hoặc Email nếu thay đổi
        if (dto.getTenDangNhap() != null && !dto.getTenDangNhap().equals(taiKhoan.getTenDangNhap())) {
            if (taiKhoanRepository.existsByTenDangNhap(dto.getTenDangNhap())) {
                throw new ResourceAlreadyExistsException("Tên đăng nhập đã tồn tại: " + dto.getTenDangNhap());
            }
            taiKhoan.setTenDangNhap(dto.getTenDangNhap());
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(taiKhoan.getEmail())) {
            if (taiKhoanRepository.existsByEmail(dto.getEmail())) {
                throw new ResourceAlreadyExistsException("Email đã tồn tại: " + dto.getEmail());
            }
            taiKhoan.setEmail(dto.getEmail());
        }

        if (dto.getMatKhau() != null && !dto.getMatKhau().isBlank()) {
            taiKhoan.setMatKhau(passwordEncoder.encode(dto.getMatKhau()));
        }

        if (dto.getVaiTro() != null && !dto.getVaiTro().isBlank()) {
            taiKhoan.setVaiTro(dto.getVaiTro());
        }

        if (dto.getTrangThai() != null && !dto.getTrangThai().isBlank()) {
            taiKhoan.setTrangThai(dto.getTrangThai());
        }

        return taiKhoanRepository.save(taiKhoan);
    }

}
