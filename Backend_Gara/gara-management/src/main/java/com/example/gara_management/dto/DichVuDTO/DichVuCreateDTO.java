package com.example.gara_management.dto.DichVuDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DichVuCreateDTO {

    @NotBlank(message = "Tên dịch vụ không được để trống.")
    @Size(max = 100, message = "Tên dịch vụ không được vượt quá 100 ký tự.")
    private String tenDichVu;

    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự.")
    private String moTa; // Có thể null

    // private String anhDichVu; // Có thể null (TEXT)

    @NotNull(message = "Số lượng tồn không được để trống.")
    @Min(value = 0, message = "Số lượng tồn phải lớn hơn hoặc bằng 0.")
    private Integer soLuongTon;

    @NotNull(message = "Số lượng bán không được để trống.")
    @Min(value = 0, message = "Số lượng bán phải lớn hơn hoặc bằng 0.")
    private Integer soLuongBan;

    @NotNull(message = "Giá không được để trống.")
    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0.")
    private BigDecimal gia;

    @NotNull(message = "Thời gian ước tính không được để trống.")
    @Min(value = 1, message = "Thời gian ước tính phải lớn hơn 0.")
    private Integer thoiGianUocTinh;
    
    // Khóa ngoại được nhập bằng Tên Loại Dịch Vụ
    @NotBlank(message = "Tên loại dịch vụ không được để trống.")
    private String tenLoaiDichVu; 
}