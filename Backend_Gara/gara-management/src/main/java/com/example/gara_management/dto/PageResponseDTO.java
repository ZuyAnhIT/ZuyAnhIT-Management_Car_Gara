package com.example.gara_management.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> { // <T> là kiểu dữ liệu chung (Generic)

    private List<T> content; // Danh sách dữ liệu của trang hiện tại
    private int pageNumber; // Số trang hiện tại (bắt đầu từ 0 hoặc 1)
    private int pageSize; // Kích thước trang
    private long totalElements; // Tổng số phần tử (tất cả các trang)
    private int totalPages; // Tổng số trang
    private boolean last; // Có phải là trang cuối không

    // Constructor tiện ích từ Spring Data Page
    public PageResponseDTO(org.springframework.data.domain.Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber(); // Spring Page index bắt đầu từ 0
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
    }
}