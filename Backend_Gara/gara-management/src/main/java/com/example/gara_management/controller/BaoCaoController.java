package com.example.gara_management.controller;

import com.example.gara_management.dto.BaoCaoDTO.TongDoanhThuDTO;
import com.example.gara_management.dto.BaoCaoDTO.TongTonKhoDTO;
import com.example.gara_management.dto.BaoCaoDTO.TongKhachHangDTO;
import com.example.gara_management.dto.BaoCaoDTO.TongDoanhThuTheoTuanDTO;
import com.example.gara_management.dto.BaoCaoDTO.DoanhThuTheoNgayDTO;
import com.example.gara_management.dto.BaoCaoDTO.DoanhThuTheoThangDTO;
import com.example.gara_management.dto.BaoCaoDTO.DoanhThuTheoNamDTO;
import com.example.gara_management.service.BaoCaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
    
    /**
     * API lấy tổng số lượng khách hàng từ bảng KhachHang
     * @return ResponseEntity chứa tổng số lượng khách hàng
     */
    @GetMapping("/tongkhachhang")
    @Operation(summary = "Lấy tổng số lượng khách hàng",
               description = "Tính tổng số lượng khách hàng từ bảng KhachHang trong hệ thống")
    public ResponseEntity<Map<String, Long>> layTongSoLuongKhachHang() {
        try {
            TongKhachHangDTO result = baoCaoService.layTongSoLuongKhachHang();
            Map<String, Long> response = new HashMap<>();
            response.put("tongKhachHang", result.getTongKhachHang());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Long> errorResponse = new HashMap<>();
            errorResponse.put("tongKhachHang", 0L);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
    
    /**
     * API lấy tổng doanh thu theo tuần của ngày được chỉ định
     * @param ngayChon Ngày trong tuần (format: yyyy-MM-dd)
     * @return ResponseEntity chứa tổng doanh thu trong tuần
     */
    @GetMapping("/tongdoanhthu/theotuan")
    @Operation(summary = "Lấy tổng doanh thu theo tuần",
               description = "Tính tổng doanh thu trong tuần của ngày được chỉ định. Tuần tính từ Thứ 2 đến Chủ nhật.")
    public ResponseEntity<Map<String, Object>> layTongDoanhThuTheoTuan(
            @Parameter(description = "Ngày trong tuần (yyyy-MM-dd)", example = "2024-10-15")
            @RequestParam String ngayChon) {

        try {
            TongDoanhThuTheoTuanDTO result = baoCaoService.layTongDoanhThuTheoTuan(ngayChon);

            Map<String, Object> response = new HashMap<>();
            response.put("tongDoanhThu", result.getTongDoanhThu());
            response.put("message", result.getMessage());
            response.put("tuTuan", result.getTuTuan());
            response.put("denTuan", result.getDenTuan());

            if (result.getTongDoanhThu().compareTo(java.math.BigDecimal.ZERO) > 0) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("tongDoanhThu", java.math.BigDecimal.ZERO);
            errorResponse.put("message", "Lỗi hệ thống khi lấy tổng doanh thu theo tuần: " + e.getMessage());
            errorResponse.put("tuTuan", "");
            errorResponse.put("denTuan", "");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
    
    /**
     * API lấy doanh thu theo từng ngày trong tuần của ngày được chỉ định
     * @param ngayChon Ngày trong tuần (format: yyyy-MM-dd)
     * @return ResponseEntity chứa doanh thu từng ngày trong tuần
     */
    @GetMapping("/doanhthu/theongay")
    @Operation(summary = "Lấy doanh thu theo từng ngày trong tuần",
               description = "Tính doanh thu từng ngày trong tuần của ngày được chỉ định. Trả về chi tiết Thứ 2 đến Chủ nhật.")
    public ResponseEntity<Map<String, Object>> layDoanhThuTheoNgayTrongTuan(
            @Parameter(description = "Ngày trong tuần (yyyy-MM-dd)", example = "2024-10-15")
            @RequestParam String ngayChon) {

        try {
            DoanhThuTheoNgayDTO result = baoCaoService.layDoanhThuTheoNgayTrongTuan(ngayChon);

            Map<String, Object> response = new HashMap<>();
            response.put("doanhThuTheoNgay", result.getDoanhThuTheoNgay());
            response.put("tongDoanhThuTuan", result.getTongDoanhThuTuan());
            response.put("message", result.getMessage());
            response.put("tuTuan", result.getTuTuan());
            response.put("denTuan", result.getDenTuan());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("doanhThuTheoNgay", List.of());
            errorResponse.put("tongDoanhThuTuan", java.math.BigDecimal.ZERO);
            errorResponse.put("message", "Lỗi hệ thống khi lấy doanh thu theo từng ngày trong tuần: " + e.getMessage());
            errorResponse.put("tuTuan", "");
            errorResponse.put("denTuan", "");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
    
    /**
     * API lấy doanh thu theo tháng với chi tiết từng tuần theo lịch thực tế
     * @param thang Tháng (1-12)
     * @param nam Năm (ví dụ: 2024)
     * @return ResponseEntity chứa doanh thu từng tuần trong tháng
     */
    @GetMapping("/doanhthu/theothang")
    @Operation(summary = "Lấy doanh thu theo tháng",
               description = "Tính doanh thu từng tuần trong tháng được chỉ định. Trả về tổng doanh thu tháng và chi tiết từng tuần theo lịch thực tế.")
    public ResponseEntity<Map<String, Object>> layDoanhThuTheoThang(
            @Parameter(description = "Tháng (1-12)", example = "10")
            @RequestParam Integer thang,
            @Parameter(description = "Năm", example = "2024")
            @RequestParam Integer nam) {

        try {
            DoanhThuTheoThangDTO result = baoCaoService.layDoanhThuTheoThang(thang, nam);

            Map<String, Object> response = new HashMap<>();
            response.put("doanhThuTheoTuan", result.getDoanhThuTheoTuan());
            response.put("tongDoanhThuThang", result.getTongDoanhThuThang());
            response.put("message", result.getMessage());
            response.put("thang", result.getThang());
            response.put("nam", result.getNam());
            response.put("soTuan", result.getSoTuan());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("doanhThuTheoTuan", List.of());
            errorResponse.put("tongDoanhThuThang", java.math.BigDecimal.ZERO);
            errorResponse.put("message", "Lỗi hệ thống khi lấy doanh thu theo tháng: " + e.getMessage());
            errorResponse.put("thang", "");
            errorResponse.put("nam", "");
            errorResponse.put("soTuan", 0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
    
    /**
     * API lấy doanh thu theo năm với chi tiết từng quý
     * @param nam Năm (ví dụ: 2024)
     * @return ResponseEntity chứa doanh thu từng quý trong năm
     */
    @GetMapping("/doanhthu/theonam")
    @Operation(summary = "Lấy doanh thu theo năm",
               description = "Tính doanh thu từng quý trong năm được chỉ định. Trả về tổng doanh thu năm và chi tiết từng quý.")
    public ResponseEntity<Map<String, Object>> layDoanhThuTheoNam(
            @Parameter(description = "Năm", example = "2024")
            @RequestParam Integer nam) {

        try {
            DoanhThuTheoNamDTO result = baoCaoService.layDoanhThuTheoNam(nam);

            Map<String, Object> response = new HashMap<>();
            response.put("doanhThuTheoQuy", result.getDoanhThuTheoQuy());
            response.put("tongDoanhThuNam", result.getTongDoanhThuNam());
            response.put("message", result.getMessage());
            response.put("nam", result.getNam());
            response.put("soQuy", result.getSoQuy());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("doanhThuTheoQuy", List.of());
            errorResponse.put("tongDoanhThuNam", java.math.BigDecimal.ZERO);
            errorResponse.put("message", "Lỗi hệ thống khi lấy doanh thu theo năm: " + e.getMessage());
            errorResponse.put("nam", "");
            errorResponse.put("soQuy", 0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
}