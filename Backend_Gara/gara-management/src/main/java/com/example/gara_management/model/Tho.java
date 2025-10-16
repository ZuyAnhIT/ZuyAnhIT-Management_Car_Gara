package com.example.gara_management.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "Tho")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTho")
    private Integer id;

    @Column(name = "TenTho", nullable = false, length = 100)
    private String name;

    @Column(name = "ChuyenMon", nullable = false, length = 100)
    private String specialty;

    @Column(name = "SoDienThoai", nullable = false, unique = true, length = 15)
    private String number;

    @Column(name = "Email", nullable = false, unique = true, length = 100 )
    private String email;
    @Column(name = "KinhNghiem", nullable = false)
    private Integer experience;

    @Column(name = "TrangThai", length = 50)
    private String status;
    
    @Column(name = "NgayVaoLam", length = 50)
    private LocalDateTime dateofwork = LocalDateTime.now();

    public Tho(String name, String specialty, String number, String email, Integer experience, String status, LocalDateTime dayofwork){
        this.name = name;
        this.specialty = specialty;
        this.number = number;
        this.email = email;
        this.experience = experience;
        this.status = status;
        this.dateofwork = dayofwork;
    }
}
