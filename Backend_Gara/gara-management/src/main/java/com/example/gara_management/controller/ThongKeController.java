package com.example.gara_management.controller;

import com.example.gara_management.dto.BaoCaoThongKeDTO.ThongKeDTO;
import com.example.gara_management.service.ThongKeService;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.gara_management.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

@RestController
@Tag(name = "Thống kê", description = "API hiển thị thống kê về hệ thống")
@RequestMapping("/api/thongKe")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ThongKeController {

    private final ThongKeService thongKeService;

    /**
     * Endpoint GET để lấy các chỉ số thống kê tổng quan.
     * URL: GET /api/thong-ke
     */
    @GetMapping("hienThiThongKe")
    public ResponseEntity<ApiResponse<ThongKeDTO>> getGeneralStatistics() {
        try {
            ThongKeDTO result = thongKeService.getGeneralStatistics();
            return ResponseEntity.ok(ApiResponse.success("Lấy dữ liệu thống kê thành công", result));
        } catch (Exception e) {
            // Xử lý lỗi hệ thống hoặc lỗi truy vấn
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống khi lấy dữ liệu thống kê: " + e.getMessage()));
        }
    }
}