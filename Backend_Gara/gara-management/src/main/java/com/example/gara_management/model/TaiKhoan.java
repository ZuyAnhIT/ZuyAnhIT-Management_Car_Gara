package com.example.gara_management.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "TaiKhoan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaiKhoan implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTaiKhoan")
    private Integer maTaiKhoan;
    
    @Column(name = "TenDangNhap", length = 50, nullable = false, unique = true)
    private String tenDangNhap;
    
    @Column(name = "MatKhau", length = 255, nullable = false) // Tăng length để chứa BCrypt hash
    private String matKhau;
    
    @Column(name = "VaiTro", length = 50, nullable = false)
    private String vaiTro = "Quản lý";
    
    @Column(name = "TrangThai", length = 50)
    private String trangThai = "Hoạt động";
    
    @Column(name = "Email", length = 50, nullable = false)
    private String email;
    
    @Column(name = "NgayTao", nullable = false)
    private LocalDateTime ngayTao;
    
    @PrePersist
    protected void onCreate() {
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
        if (vaiTro == null) {
            vaiTro = "Quản lý";
        }
        if (trangThai == null) {
            trangThai = "Hoạt động";
        }
    }
    
    // Implement UserDetails interface
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + vaiTro.toUpperCase()));
    }
    
    @Override
    public String getPassword() {
        return matKhau;
    }
    
    @Override
    public String getUsername() {
        return tenDangNhap;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return "Hoạt động".equals(trangThai);
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return "Hoạt động".equals(trangThai);
    }
}