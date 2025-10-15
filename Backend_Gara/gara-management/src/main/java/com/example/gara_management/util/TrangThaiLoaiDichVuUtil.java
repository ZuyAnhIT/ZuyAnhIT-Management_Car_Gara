package com.example.gara_management.util;

public enum TrangThaiLoaiDichVuUtil {
    HOAT_DONG("Hoạt động"),
    DA_XOA("Đã xóa");

    private final String value;

    TrangThaiLoaiDichVuUtil(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}