package com.example.gara_management.controller;

// Import DTO cho báo cáo doanh thu tuần
import com.example.gara_management.dto.BaoCaoThongKeDTO.BaoCaoDoanhThuTuanDTO;
import com.example.gara_management.dto.BaoCaoThongKeDTO.ThongKeDTO;
import com.example.gara_management.service.ThongKeService;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.gara_management.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
// Import RequestParam để nhận tham số từ URL
import org.springframework.web.bind.annotation.RequestParam;
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
     // ================================================================
    //  API BÁO CÁO DOANH THU TUẦN
    // ================================================================

    /**
     * Endpoint GET để lấy báo cáo doanh thu chi tiết của một tuần.
     * API này nhận vào một ngày bất kỳ và sẽ trả về báo cáo cho cả tuần (Thứ 2 - Chủ Nhật) chứa ngày đó.
     * 
     * @param ngay Một chuỗi ngày theo định dạng "YYYY-MM-DD". Ví dụ: "2024-10-14".
     * @return Một đối tượng ResponseEntity chứa ApiResponse. 
     *         - Thành công: Trả về mã 200 (OK) và dữ liệu báo cáo.
     *         - Lỗi định dạng ngày: Trả về mã 400 (Bad Request).
     *         - Lỗi hệ thống: Trả về mã 500 (Internal Server Error).
     * 
     * URL ví dụ: GET http://localhost:8082/api/thongKe/bao-cao-doanh-thu-tuan?ngay=2024-10-14
     */
    @GetMapping("/bao-cao-doanh-thu-tuan")
    public ResponseEntity<ApiResponse<?>> getBaoCaoDoanhThuTuan(@RequestParam("ngay") String ngay) {
        try {
            // Gọi phương thức service đã được tạo để lấy dữ liệu báo cáo
            BaoCaoDoanhThuTuanDTO result = thongKeService.getBaoCaoDoanhThuTuan(ngay);
            // Nếu thành công, trả về dữ liệu trong ApiResponse với mã 200
            return ResponseEntity.ok(ApiResponse.success("Lấy báo cáo doanh thu tuần thành công", result));
        
        } catch (IllegalArgumentException e) {
            // Bắt lỗi nếu người dùng nhập sai định dạng ngày (ví dụ: "dd-MM-yyyy")
            // Trả về lỗi 400 Bad Request với thông báo lỗi rõ ràng
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        
        } catch (Exception e) {
            // Bắt các lỗi hệ thống khác có thể xảy ra trong quá trình xử lý
            // Trả về lỗi 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống khi lấy báo cáo doanh thu tuần: " + e.getMessage()));
        }
    }
}