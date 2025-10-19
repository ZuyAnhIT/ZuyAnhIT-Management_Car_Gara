package com.example.gara_management.controller;

import com.example.gara_management.dto.BaoCaoDTO.TongDoanhThuDTO;
import com.example.gara_management.dto.BaoCaoDTO.TongTonKhoDTO;
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
@Tag(name = "Báo Cáo Thống Kê", description = "API báo cáo thống kê tổng doanh thu và tồn kho")
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
    
    /**
     * API lấy tổng số lượng tồn kho từ tất cả các dịch vụ
     * @return ResponseEntity chứa tổng số lượng tồn kho
     */
    @GetMapping("/tongtonkho")
    @Operation(summary = "Lấy tổng số lượng tồn kho", 
               description = "Tính tổng số lượng tồn kho từ tất cả các dịch vụ trong hệ thống")
    public ResponseEntity<Map<String, Long>> layTongSoLuongTon() {
        try {
            TongTonKhoDTO result = baoCaoService.layTongSoLuongTon();
            Map<String, Long> response = new HashMap<>();
            response.put("tongTonKho", result.getTongSoLuongTon());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Long> errorResponse = new HashMap<>();
            errorResponse.put("tongTonKho", 0L);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
}