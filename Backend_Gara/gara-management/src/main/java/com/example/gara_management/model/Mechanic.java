package com.example.gara_management.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "Tho")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mechanic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTho")
    private Integer id;

    @Column(name = "TenTho", nullable = false, length = 100)
    private String name;

    @Column(name = "ChuyenMon", nullable = false, length = 100)
    private String specialty;

    @Column(name = "SoDienThoai", nullable = false, length = 15)
    private String number;

    @Column(name = "KinhNghiem", nullable = false)
    private Integer experience;

    @Column(name = "Luong", nullable = false)
    private double salary;

    @Column(name = "TrangThai", length = 50)
    private String status;
    
    @Column(name = "NgayVaoLam", length = 50)
    private LocalDateTime dateofwork = LocalDateTime.now();
}
