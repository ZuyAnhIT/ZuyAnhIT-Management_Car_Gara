package com.example.gara_management.exception;

// Dùng khi không tìm thấy tài nguyên theo ID/Mã
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}