package com.example.gara_management.util;

import org.springframework.data.domain.Sort;

public class SortUtils {

    /**
     * Chuyển đổi chuỗi hướng sắp xếp ("asc" hoặc "desc") và tên trường thành đối tượng Sort.
     * @param sortBy Tên trường để sắp xếp (ví dụ: "tenLoai", "ngayTao").
     * @param sortDirection Hướng sắp xếp ("asc" hoặc "desc").
     * @param defaultSortBy Trường mặc định nếu sortBy rỗng hoặc null.
     * @param defaultDirection Hướng mặc định (ví dụ: Sort.Direction.DESC).
     * @return Đối tượng Sort đã được định nghĩa.
     */
    public static Sort createSort(String sortBy, String sortDirection, 
                                  String defaultSortBy, Sort.Direction defaultDirection) {
        
        // 1. Xác định hướng sắp xếp thực tế
        Sort.Direction direction;
        if (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        } else if (sortDirection != null && sortDirection.equalsIgnoreCase("asc")) {
             direction = Sort.Direction.ASC;
        } else {
             direction = defaultDirection; // Dùng mặc định nếu chuỗi không hợp lệ
        }
        
        // 2. Xác định trường sắp xếp thực tế
        String actualSortBy = (sortBy != null && !sortBy.trim().isEmpty()) ? sortBy : defaultSortBy;
        
        // 3. Tạo đối tượng Sort
        return Sort.by(direction, actualSortBy);
    }
}