package com.example.gara_management.dto.DichVuDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DichVuUpdateDTO {

    @Size(max = 100, message = "Tên dịch vụ không được vượt quá 100 ký tự.")
    private String tenDichVu; 

    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự.")
    private String moTa; 

    private String anhDichVu; 

    @Min(value = 0, message = "Số lượng tồn phải lớn hơn hoặc bằng 0.")
    private Integer soLuongTon;

    @Min(value = 0, message = "Số lượng bán phải lớn hơn hoặc bằng 0.")
    private Integer soLuongBan;

    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0.")
    private BigDecimal gia;

    @Min(value = 1, message = "Thời gian ước tính phải lớn hơn 0.")
    private Integer thoiGianUocTinh;
    
    private String trangThai; // Sẽ được kiểm tra trong Service

    // Trường để cập nhật Loại Dịch Vụ (bằng tên)
    private String tenLoaiDichVu; 
}