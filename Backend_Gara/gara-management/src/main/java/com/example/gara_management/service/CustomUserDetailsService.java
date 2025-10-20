package com.example.gara_management.service;

import com.example.gara_management.model.TaiKhoan;
import com.example.gara_management.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Cần các imports này để tạo đối tượng User của Spring Security
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final TaiKhoanRepository taiKhoanRepository;
    
    @Override
    @Transactional(readOnly = true) // Nên là readOnly cho thao tác đọc
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + username));
        
        // 1. Lấy vai trò (Ví dụ: "Quản lý")
        String vaiTro = taiKhoan.getVaiTro();

        // 2. Trả về đối tượng UserDetails chuẩn của Spring Security
        // Ghi chú: Chúng ta sử dụng SimpleGrantedAuthority để ánh xạ VaiTro.
        // Đây là cách đơn giản và phổ biến nhất, phù hợp với hasAuthority('Quản lý').
        return new User(
                taiKhoan.getTenDangNhap(), // Username
                taiKhoan.getMatKhau(),    // Mật khẩu (đã hash)
                Collections.singletonList(new SimpleGrantedAuthority(vaiTro)) // Danh sách quyền
        );
        
        // Hoặc, nếu bạn muốn dùng hasRole("QUẢN LÝ") trong SecurityConfig:
        /*
        return new User(
                taiKhoan.getTenDangNhap(),
                taiKhoan.getMatKhau(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + vaiTro.toUpperCase()))
        );
        */
    }
}