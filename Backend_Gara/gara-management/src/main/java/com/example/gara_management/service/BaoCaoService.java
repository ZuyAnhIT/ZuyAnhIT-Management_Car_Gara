package com.example.gara_management.service;

import com.example.gara_management.dto.BaoCaoDTO.TongDoanhThuDTO;
import com.example.gara_management.dto.BaoCaoDTO.TongTonKhoDTO;
import com.example.gara_management.dto.BaoCaoDTO.TongKhachHangDTO;
import com.example.gara_management.dto.BaoCaoDTO.TongDoanhThuTheoTuanDTO;
import com.example.gara_management.dto.BaoCaoDTO.DoanhThuTheoNgayDTO;
import com.example.gara_management.repository.BaoCaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BaoCaoService {
    
    private final BaoCaoRepository baoCaoRepository;
    
    /**
     * Lấy tổng doanh thu từ tất cả các hóa đơn đã thanh toán
     * @return TongDoanhThuDTO chứa tổng doanh thu
     */
    public TongDoanhThuDTO layTongDoanhThu() {
        try {
            // Lấy tổng doanh thu từ database
            BigDecimal tongDoanhThu = baoCaoRepository.tinhTongDoanhThu();
            
            // Kiểm tra nếu không có dữ liệu
            if (tongDoanhThu.compareTo(BigDecimal.ZERO) == 0) {
                return TongDoanhThuDTO.createEmptyResponse();
            }
            
            // Trả về kết quả thành công
            return TongDoanhThuDTO.createSuccessResponse(tongDoanhThu);
            
        } catch (Exception e) {
            // Xử lý lỗi và trả về response lỗi
            return TongDoanhThuDTO.builder()
                    .tongDoanhThu(BigDecimal.ZERO)
                    .message("Lỗi khi lấy tổng doanh thu: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Lấy tổng số lượng tồn kho từ tất cả các dịch vụ
     * @return TongTonKhoDTO chứa tổng số lượng tồn kho
     */
    public TongTonKhoDTO layTongSoLuongTon() {
        try {
            // Lấy tổng số lượng tồn kho từ database
            Long tongSoLuongTon = baoCaoRepository.tinhTongSoLuongTon();
            
            // Kiểm tra nếu không có dữ liệu
            if (tongSoLuongTon == 0) {
                return TongTonKhoDTO.createEmptyResponse();
            }
            
            // Trả về kết quả thành công
            return TongTonKhoDTO.createSuccessResponse(tongSoLuongTon);
            
        } catch (Exception e) {
            // Xử lý lỗi và trả về response lỗi
            return TongTonKhoDTO.builder()
                    .tongSoLuongTon(0L)
                    .message("Lỗi khi lấy tổng số lượng tồn kho: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Lấy tổng số lượng khách hàng từ bảng KhachHang
     * @return TongKhachHangDTO chứa tổng số lượng khách hàng
     */
    public TongKhachHangDTO layTongSoLuongKhachHang() {
        try {
            // Lấy tổng số lượng khách hàng từ database
            Long tongSoLuongKhachHang = baoCaoRepository.tinhTongSoLuongKhachHang();

            // Kiểm tra nếu không có dữ liệu
            if (tongSoLuongKhachHang == 0) {
                return TongKhachHangDTO.createEmptyResponse();
            }

            // Trả về kết quả thành công
            return TongKhachHangDTO.createSuccessResponse(tongSoLuongKhachHang);

        } catch (Exception e) {
            // Xử lý lỗi và trả về response lỗi
            return TongKhachHangDTO.builder()
                    .tongKhachHang(0L)
                    .message("Lỗi khi lấy tổng số lượng khách hàng: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Lấy tổng doanh thu theo tuần của ngày được chỉ định
     * @param ngayChon Ngày trong tuần (format: yyyy-MM-dd)
     * @return TongDoanhThuTheoTuanDTO chứa tổng doanh thu trong tuần
     */
    public TongDoanhThuTheoTuanDTO layTongDoanhThuTheoTuan(String ngayChon) {
        try {
            // Validate định dạng ngày
            if (ngayChon == null || ngayChon.trim().isEmpty()) {
                return TongDoanhThuTheoTuanDTO.builder()
                        .tongDoanhThu(BigDecimal.ZERO)
                        .message("Ngày không được để trống")
                        .tuTuan("")
                        .denTuan("")
                        .build();
            }

            // Parse ngày với định dạng yyyy-MM-dd
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate ngay;
            try {
                ngay = LocalDate.parse(ngayChon, formatter);
            } catch (DateTimeParseException e) {
                return TongDoanhThuTheoTuanDTO.builder()
                        .tongDoanhThu(BigDecimal.ZERO)
                        .message("Định dạng ngày không hợp lệ. Vui lòng nhập theo định dạng yyyy-MM-dd (ví dụ: 2024-10-15)")
                        .tuTuan("")
                        .denTuan("")
                        .build();
            }

            // Tính ngày đầu tuần (Thứ 2) và cuối tuần (Chủ nhật)
            LocalDate startOfWeek = ngay.with(DayOfWeek.MONDAY);
            LocalDate endOfWeek = ngay.with(DayOfWeek.SUNDAY);

            // Chuyển đổi sang LocalDateTime cho query
            LocalDateTime startOfWeekDateTime = startOfWeek.atStartOfDay(); // 00:00:00
            LocalDateTime endOfWeekDateTime = endOfWeek.atTime(23, 59, 59); // 23:59:59

            // Lấy tổng doanh thu trong tuần
            BigDecimal tongDoanhThu = baoCaoRepository.tinhTongDoanhThuTheoTuan(startOfWeekDateTime, endOfWeekDateTime);

            // Kiểm tra nếu không có dữ liệu
            if (tongDoanhThu.compareTo(BigDecimal.ZERO) == 0) {
                return TongDoanhThuTheoTuanDTO.createEmptyResponse(
                        startOfWeek.format(formatter), 
                        endOfWeek.format(formatter)
                );
            }

            // Trả về kết quả thành công
            return TongDoanhThuTheoTuanDTO.createSuccessResponse(
                    tongDoanhThu,
                    startOfWeek.format(formatter),
                    endOfWeek.format(formatter)
            );

        } catch (Exception e) {
            // Xử lý lỗi và trả về response lỗi
            return TongDoanhThuTheoTuanDTO.builder()
                    .tongDoanhThu(BigDecimal.ZERO)
                    .message("Lỗi khi lấy tổng doanh thu theo tuần: " + e.getMessage())
                    .tuTuan("")
                    .denTuan("")
                    .build();
        }
    }
    
    /**
     * Lấy doanh thu theo từng ngày trong tuần của ngày được chỉ định
     * @param ngayChon Ngày trong tuần (format: yyyy-MM-dd)
     * @return DoanhThuTheoNgayDTO chứa doanh thu từng ngày trong tuần
     */
    public DoanhThuTheoNgayDTO layDoanhThuTheoNgayTrongTuan(String ngayChon) {
        try {
            // Validate định dạng ngày
            if (ngayChon == null || ngayChon.trim().isEmpty()) {
                return DoanhThuTheoNgayDTO.builder()
                        .doanhThuTheoNgay(List.of())
                        .tongDoanhThuTuan(BigDecimal.ZERO)
                        .message("Ngày không được để trống")
                        .tuTuan("")
                        .denTuan("")
                        .build();
            }

            // Parse ngày với định dạng yyyy-MM-dd
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate ngay;
            try {
                ngay = LocalDate.parse(ngayChon, formatter);
            } catch (DateTimeParseException e) {
                return DoanhThuTheoNgayDTO.builder()
                        .doanhThuTheoNgay(List.of())
                        .tongDoanhThuTuan(BigDecimal.ZERO)
                        .message("Định dạng ngày không hợp lệ. Vui lòng nhập theo định dạng yyyy-MM-dd (ví dụ: 2024-10-15)")
                        .tuTuan("")
                        .denTuan("")
                        .build();
            }

            // Tính ngày đầu tuần (Thứ 2) và cuối tuần (Chủ nhật)
            LocalDate startOfWeek = ngay.with(DayOfWeek.MONDAY);
            LocalDate endOfWeek = ngay.with(DayOfWeek.SUNDAY);

            // Chuyển đổi sang LocalDateTime cho query
            LocalDateTime startOfWeekDateTime = startOfWeek.atStartOfDay(); // 00:00:00
            LocalDateTime endOfWeekDateTime = endOfWeek.atTime(23, 59, 59); // 23:59:59

            // Lấy doanh thu theo từng ngày
            List<Object[]> doanhThuData = baoCaoRepository.layDoanhThuTheoNgayTrongTuan(startOfWeekDateTime, endOfWeekDateTime);
            
            // Tạo map để dễ lookup
            Map<String, Object[]> doanhThuMap = new HashMap<>();
            for (Object[] row : doanhThuData) {
                String ngayStr = row[0].toString();
                doanhThuMap.put(ngayStr, row);
            }

            // Tạo danh sách 7 ngày trong tuần
            List<DoanhThuTheoNgayDTO.DoanhThuNgay> doanhThuTheoNgay = new ArrayList<>();
            BigDecimal tongDoanhThuTuan = BigDecimal.ZERO;
            
            String[] tenNgay = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
            
            for (int i = 0; i < 7; i++) {
                LocalDate currentDay = startOfWeek.plusDays(i);
                String ngayStr = currentDay.format(formatter);
                
                Object[] data = doanhThuMap.get(ngayStr);
                BigDecimal doanhThu = BigDecimal.ZERO;
                Integer soHoaDon = 0;
                
                if (data != null) {
                    doanhThu = (BigDecimal) data[1];
                    soHoaDon = ((Number) data[2]).intValue();
                    tongDoanhThuTuan = tongDoanhThuTuan.add(doanhThu);
                }
                
                doanhThuTheoNgay.add(DoanhThuTheoNgayDTO.DoanhThuNgay.builder()
                        .tenNgay(tenNgay[i])
                        .ngay(ngayStr)
                        .doanhThu(doanhThu)
                        .soHoaDon(soHoaDon)
                        .build());
            }

            // Kiểm tra nếu không có dữ liệu
            if (tongDoanhThuTuan.compareTo(BigDecimal.ZERO) == 0) {
                return DoanhThuTheoNgayDTO.createEmptyResponse(
                        startOfWeek.format(formatter), 
                        endOfWeek.format(formatter)
                );
            }

            // Trả về kết quả thành công
            return DoanhThuTheoNgayDTO.createSuccessResponse(
                    doanhThuTheoNgay,
                    tongDoanhThuTuan,
                    startOfWeek.format(formatter),
                    endOfWeek.format(formatter)
            );

        } catch (Exception e) {
            // Xử lý lỗi và trả về response lỗi
            return DoanhThuTheoNgayDTO.builder()
                    .doanhThuTheoNgay(List.of())
                    .tongDoanhThuTuan(BigDecimal.ZERO)
                    .message("Lỗi khi lấy doanh thu theo từng ngày trong tuần: " + e.getMessage())
                    .tuTuan("")
                    .denTuan("")
                    .build();
        }
    }
}