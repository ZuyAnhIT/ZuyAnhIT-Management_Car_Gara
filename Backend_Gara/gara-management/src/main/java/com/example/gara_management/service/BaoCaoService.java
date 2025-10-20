package com.example.gara_management.service;

import com.example.gara_management.dto.BaoCaoDTO.TongDoanhThuDTO;
import com.example.gara_management.dto.BaoCaoDTO.TongTonKhoDTO;
import com.example.gara_management.dto.BaoCaoDTO.TongKhachHangDTO;
import com.example.gara_management.dto.BaoCaoDTO.TongDoanhThuTheoTuanDTO;
import com.example.gara_management.dto.BaoCaoDTO.DoanhThuTheoNgayDTO;
import com.example.gara_management.dto.BaoCaoDTO.DoanhThuTheoThangDTO;
import com.example.gara_management.dto.BaoCaoDTO.DoanhThuTheoNamDTO;
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
import java.time.YearMonth;
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
    
    /**
     * Lấy doanh thu theo tháng với chi tiết từng tuần theo lịch thực tế
     * @param thang Tháng (1-12)
     * @param nam Năm (ví dụ: 2024)
     * @return DoanhThuTheoThangDTO chứa doanh thu từng tuần trong tháng
     */
    public DoanhThuTheoThangDTO layDoanhThuTheoThang(Integer thang, Integer nam) {
        try {
            // Validate tháng và năm
            if (thang == null || nam == null) {
                return DoanhThuTheoThangDTO.builder()
                        .doanhThuTheoTuan(List.of())
                        .tongDoanhThuThang(BigDecimal.ZERO)
                        .message("Tháng và năm không được để trống")
                        .thang("")
                        .nam("")
                        .soTuan(0)
                        .build();
            }

            if (thang < 1 || thang > 12) {
                return DoanhThuTheoThangDTO.builder()
                        .doanhThuTheoTuan(List.of())
                        .tongDoanhThuThang(BigDecimal.ZERO)
                        .message("Tháng phải từ 1 đến 12")
                        .thang("")
                        .nam("")
                        .soTuan(0)
                        .build();
            }

            if (nam < 1900 || nam > 2100) {
                return DoanhThuTheoThangDTO.builder()
                        .doanhThuTheoTuan(List.of())
                        .tongDoanhThuThang(BigDecimal.ZERO)
                        .message("Năm phải từ 1900 đến 2100")
                        .thang("")
                        .nam("")
                        .soTuan(0)
                        .build();
            }

            // Tạo YearMonth từ tháng và năm
            YearMonth yearMonth = YearMonth.of(nam, thang);

            // Tính ngày đầu và cuối tháng
            LocalDate startOfMonth = yearMonth.atDay(1);
            LocalDate endOfMonth = yearMonth.atEndOfMonth();

            // Tính doanh thu theo tuần đơn giản từ ngày 1 đến ngày cuối tháng
            List<DoanhThuTheoThangDTO.DoanhThuTuan> doanhThuTheoTuan = new ArrayList<>();
            BigDecimal tongDoanhThuThang = BigDecimal.ZERO;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            // Tính số tuần trong tháng (mỗi tuần 7 ngày)
            int soNgayTrongThang = endOfMonth.getDayOfMonth();
            int soTuanTrongThang = (int) Math.ceil(soNgayTrongThang / 7.0);
            
            // Tính doanh thu cho từng tuần
            for (int tuan = 1; tuan <= soTuanTrongThang; tuan++) {
                // Tính ngày đầu và cuối của tuần
                int ngayDauTuan = (tuan - 1) * 7 + 1;
                int ngayCuoiTuan = Math.min(tuan * 7, soNgayTrongThang);
                
                LocalDate tuTuan = startOfMonth.withDayOfMonth(ngayDauTuan);
                LocalDate denTuan = startOfMonth.withDayOfMonth(ngayCuoiTuan);
                
                // Tính doanh thu cho tuần này
                BigDecimal doanhThuTuan = BigDecimal.ZERO;
                Integer soHoaDonTuan = 0;
                
                // Duyệt qua từng ngày trong tuần
                LocalDate ngayHienTai = tuTuan;
                while (!ngayHienTai.isAfter(denTuan)) {
                    // Lấy doanh thu của ngày này
                    LocalDateTime ngayDauNgay = ngayHienTai.atStartOfDay();
                    LocalDateTime ngayCuoiNgay = ngayHienTai.atTime(23, 59, 59);
                    
                    BigDecimal doanhThuNgay = baoCaoRepository.tinhTongDoanhThuTheoTuan(ngayDauNgay, ngayCuoiNgay);
                    doanhThuTuan = doanhThuTuan.add(doanhThuNgay);
                    
                    // Đếm số hóa đơn của ngày này (có thể cải thiện sau)
                    if (doanhThuNgay.compareTo(BigDecimal.ZERO) > 0) {
                        soHoaDonTuan++; // Tạm thời đếm như vậy
                    }
                    
                    ngayHienTai = ngayHienTai.plusDays(1);
                }
                
                tongDoanhThuThang = tongDoanhThuThang.add(doanhThuTuan);
                
                doanhThuTheoTuan.add(DoanhThuTheoThangDTO.DoanhThuTuan.builder()
                        .tuan(tuan)
                        .tuTuan(tuTuan.format(formatter))
                        .denTuan(denTuan.format(formatter))
                        .doanhThu(doanhThuTuan)
                        .soHoaDon(soHoaDonTuan)
                        .moTaTuan("Tuần " + tuan)
                        .build());
            }

            // Kiểm tra nếu không có dữ liệu
            if (tongDoanhThuThang.compareTo(BigDecimal.ZERO) == 0) {
                return DoanhThuTheoThangDTO.createEmptyResponse(
                        String.valueOf(yearMonth.getMonthValue()),
                        String.valueOf(yearMonth.getYear())
                );
            }

            // Trả về kết quả thành công
            return DoanhThuTheoThangDTO.createSuccessResponse(
                    doanhThuTheoTuan,
                    tongDoanhThuThang,
                    String.valueOf(yearMonth.getMonthValue()),
                    String.valueOf(yearMonth.getYear()),
                    doanhThuTheoTuan.size()
            );

        } catch (Exception e) {
            // Xử lý lỗi và trả về response lỗi
            return DoanhThuTheoThangDTO.builder()
                    .doanhThuTheoTuan(List.of())
                    .tongDoanhThuThang(BigDecimal.ZERO)
                    .message("Lỗi khi lấy doanh thu theo tháng: " + e.getMessage())
                    .thang("")
                    .nam("")
                    .soTuan(0)
                    .build();
        }
    }
    
    /**
     * Lấy doanh thu theo năm với chi tiết từng quý
     * @param nam Năm (ví dụ: 2024)
     * @return DoanhThuTheoNamDTO chứa doanh thu từng quý trong năm
     */
    public DoanhThuTheoNamDTO layDoanhThuTheoNam(Integer nam) {
        try {
            // Validate năm
            if (nam == null) {
                return DoanhThuTheoNamDTO.builder()
                        .doanhThuTheoQuy(List.of())
                        .tongDoanhThuNam(BigDecimal.ZERO)
                        .message("Năm không được để trống")
                        .nam("")
                        .soQuy(0)
                        .build();
            }

            if (nam < 1900 || nam > 2100) {
                return DoanhThuTheoNamDTO.builder()
                        .doanhThuTheoQuy(List.of())
                        .tongDoanhThuNam(BigDecimal.ZERO)
                        .message("Năm phải từ 1900 đến 2100")
                        .nam("")
                        .soQuy(0)
                        .build();
            }

            // Tính doanh thu theo quý đơn giản
            List<DoanhThuTheoNamDTO.DoanhThuQuy> doanhThuTheoQuy = new ArrayList<>();
            BigDecimal tongDoanhThuNam = BigDecimal.ZERO;
            
            // Tính doanh thu cho từng quý (4 quý)
            for (int quy = 1; quy <= 4; quy++) {
                // Tính tháng đầu và cuối của quý
                int thangDauQuy = (quy - 1) * 3 + 1;
                int thangCuoiQuy = quy * 3;
                
                // Tính ngày đầu và cuối của quý
                LocalDate ngayDauQuy = LocalDate.of(nam, thangDauQuy, 1);
                LocalDate ngayCuoiQuy = LocalDate.of(nam, thangCuoiQuy, 1).withDayOfMonth(
                        LocalDate.of(nam, thangCuoiQuy, 1).lengthOfMonth()
                );
                
                // Tính doanh thu cho quý này
                LocalDateTime dauQuy = ngayDauQuy.atStartOfDay();
                LocalDateTime cuoiQuy = ngayCuoiQuy.atTime(23, 59, 59);
                
                BigDecimal doanhThuQuy = baoCaoRepository.tinhTongDoanhThuTheoNam(dauQuy, cuoiQuy);
                tongDoanhThuNam = tongDoanhThuNam.add(doanhThuQuy);
                
                // Đếm số hóa đơn của quý này (có thể cải thiện sau)
                Integer soHoaDonQuy = 0;
                if (doanhThuQuy.compareTo(BigDecimal.ZERO) > 0) {
                    soHoaDonQuy = 1; // Tạm thời đếm như vậy
                }
                
                doanhThuTheoQuy.add(DoanhThuTheoNamDTO.DoanhThuQuy.builder()
                        .quy(quy)
                        .tuThang("Tháng " + thangDauQuy)
                        .denThang("Tháng " + thangCuoiQuy)
                        .doanhThu(doanhThuQuy)
                        .soHoaDon(soHoaDonQuy)
                        .moTaQuy("Quý " + quy)
                        .build());
            }

            // Kiểm tra nếu không có dữ liệu
            if (tongDoanhThuNam.compareTo(BigDecimal.ZERO) == 0) {
                return DoanhThuTheoNamDTO.createEmptyResponse(String.valueOf(nam));
            }

            // Trả về kết quả thành công
            return DoanhThuTheoNamDTO.createSuccessResponse(
                    doanhThuTheoQuy,
                    tongDoanhThuNam,
                    String.valueOf(nam),
                    4
            );

        } catch (Exception e) {
            // Xử lý lỗi và trả về response lỗi
            return DoanhThuTheoNamDTO.builder()
                    .doanhThuTheoQuy(List.of())
                    .tongDoanhThuNam(BigDecimal.ZERO)
                    .message("Lỗi khi lấy doanh thu theo năm: " + e.getMessage())
                    .nam("")
                    .soQuy(0)
                    .build();
        }
    }
}