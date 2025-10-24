package com.example.gara_management.service;

import com.example.gara_management.dto.auth.*;
import com.example.gara_management.model.TaiKhoan;
import com.example.gara_management.repository.TaiKhoanRepository;
import com.example.gara_management.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
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

}
