package com.example.gara_management.controller;

import com.example.gara_management.dto.BaoCaoDTO.TongDoanhThuDTO;
import com.example.gara_management.service.BaoCaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/baocao")
@Tag(name = "Báo Cáo Thống Kê", description = "API báo cáo tổng doanh thu từ các hóa đơn đã thanh toán")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BaoCaoController {
    
    private final BaoCaoService baoCaoService;
    
    /**
     * API lấy tổng doanh thu từ tất cả các hóa đơn đã thanh toán
     * @return ResponseEntity chứa tổng doanh thu
     */
    @GetMapping("/tongdoanhthu")
    @Operation(summary = "Lấy tổng doanh thu", 
               description = "Tính tổng doanh thu từ tất cả các hóa đơn có trạng thái 'Đã thanh toán'")
    public ResponseEntity<Map<String, BigDecimal>> layTongDoanhThu() {
        try {
            TongDoanhThuDTO result = baoCaoService.layTongDoanhThu();
            Map<String, BigDecimal> response = new HashMap<>();
            response.put("tongDoanhThu", result.getTongDoanhThu());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, BigDecimal> errorResponse = new HashMap<>();
            errorResponse.put("tongDoanhThu", BigDecimal.ZERO);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
}
