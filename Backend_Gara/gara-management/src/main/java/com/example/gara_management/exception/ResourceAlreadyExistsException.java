package com.example.gara_management.exception;

// Exception này dùng cho các trường hợp dữ liệu đã tồn tại (ví dụ: tên trùng)
public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}