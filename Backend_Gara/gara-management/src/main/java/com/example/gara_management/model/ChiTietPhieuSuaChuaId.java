package com.example.gara_management.model;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChiTietPhieuSuaChuaId implements Serializable {
    private Integer phieuSuaChua;
    private Integer dichVu;
}