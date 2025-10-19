package com.example.gara_management.dto.PhieuSuaChuaDTO;


import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePhieuSuaChuaRequest {
    
    @NotNull(message = "Mã xe không được để trống")
    private Integer maXe;
    
    @NotNull(message = "Mã thợ không được để trống")
    private Integer maTho;

    private String bienSo; 
    
    private String tenTho; 
    
    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
    private String moTa;
    
    @NotEmpty(message = "Phải có ít nhất một dịch vụ")
    private List<ChiTietRequest> chiTietList;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChiTietRequest {
        @NotNull(message = "Mã dịch vụ không được để trống")
        private Integer maDichVu;
        
        @Min(value = 1, message = "Số lượng phải lớn hơn 0")
        private Integer soLuong;
    }
}