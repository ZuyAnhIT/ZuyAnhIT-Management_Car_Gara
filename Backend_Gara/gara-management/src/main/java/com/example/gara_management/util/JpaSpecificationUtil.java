package com.example.gara_management.util;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

public class JpaSpecificationUtil {

    /**
     * Tạo Specification để tìm kiếm theo LIKE (Chứa) trên trường String.
     * Thường dùng cho các trường tên, mô tả.
     * @param field Tên trường trong Entity (ví dụ: "tenLoai").
     * @param value Giá trị tìm kiếm.
     * @return Specification áp dụng điều kiện LIKE.
     */
    public static <T> Specification<T> attributeContains(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction(); // Trả về điều kiện TRUE nếu giá trị rỗng
        }
        String pattern = "%" + value.toLowerCase() + "%";
        return (root, query, criteriaBuilder) -> {
            Path<String> path = root.get(field);
            return criteriaBuilder.like(criteriaBuilder.lower(path), pattern);
        };
    }
    
    /**
     * Tạo Specification để tìm kiếm chính xác theo giá trị.
     * Thường dùng cho các trường trạng thái (String/Enum) hoặc số (Integer/Long).
     * @param field Tên trường trong Entity (ví dụ: "trangThai").
     * @param value Giá trị tìm kiếm (dạng Object để linh hoạt).
     * @return Specification áp dụng điều kiện EQUAL.
     */
    public static <T> Specification<T> attributeEquals(String field, Object value) {
        if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction(); // Trả về điều kiện TRUE
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(field), value);
    }
    
    // Bạn có thể thêm các hàm khác như attributeGreaterThan, attributeBetween...
}