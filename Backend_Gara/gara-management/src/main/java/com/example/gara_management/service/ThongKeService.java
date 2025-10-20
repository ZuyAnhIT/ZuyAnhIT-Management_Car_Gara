package com.example.gara_management.service;

import com.example.gara_management.dto.BaoCaoThongKeDTO.BaoCaoDoanhThuThangDTO;
import com.example.gara_management.dto.BaoCaoThongKeDTO.BaoCaoDoanhThuTuanDTO;
import com.example.gara_management.dto.BaoCaoThongKeDTO.ThongKeDTO;
import com.example.gara_management.model.HoaDon;
import com.example.gara_management.repository.DichVuRepository;
import com.example.gara_management.repository.HoaDonRepository;
import com.example.gara_management.repository.KhachHangRepository;
import com.example.gara_management.repository.LoaiDichVuRepository;
import com.example.gara_management.repository.ThoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThongKeService {

    // Inject các Repository cần thiết
    private final DichVuRepository dichVuRepository;
    private final ThoRepository thoRepository;
    private final LoaiDichVuRepository loaiDichVuRepository;
    private final KhachHangRepository khachHangRepository;
    private final HoaDonRepository hoaDonRepository;

    // ================================================================
    //  THỐNG KÊ TỔNG QUAN
    // ================================================================
    
    @Transactional(readOnly = true)
    public ThongKeDTO getGeneralStatistics() {

        // 1. Tổng số dịch vụ (Distinct Services/Items)
        Long tongSoDichVu = dichVuRepository.count();

        // 2. Tổng số lượng tồn của các dịch vụ (Total Stock Quantity)
        // Sử dụng phương thức custom đã định nghĩa trong DichVuRepository
        Long tongSoLuongTon = dichVuRepository.sumSoLuongTon();
        
        // 3. Tổng số thợ (Mechanics/Technicians)
        Long tongSoTho = thoRepository.count();

        // 4. Tổng số loại dịch vụ (Service Categories)
        Long tongSoLoaiDichVu = loaiDichVuRepository.count();

        // 5. Tổng số khách hàng (Customers)
        Long tongSoKhachHang = khachHangRepository.count();

        // 6. Tổng số hóa đơn (Đã thanh toán)
        Long tongSoHoaDonDaThanhToan = hoaDonRepository.countByTrangThai("Đã thanh toán");

        // Xây dựng và trả về DTO
        return ThongKeDTO.builder()
                .tongSoDichVu(tongSoDichVu)
                // Đảm bảo không trả về null nếu không có tồn kho nào
                .tongSoLuongTon(tongSoLuongTon != null ? tongSoLuongTon : 0L) 
                .tongSoTho(tongSoTho)
                .tongSoLoaiDichVu(tongSoLoaiDichVu)
                .tongSoKhachHang(tongSoKhachHang)
                .tongSoHoaDonDaThanhToan(tongSoHoaDonDaThanhToan)
                .build();
    }
    // ================================================================
    //  BÁO CÁO DOANH THU THÁNG
    // ================================================================

    /**
     * Lấy báo cáo doanh thu của một tháng cụ thể, chi tiết theo từng tuần.
     * @param nam Năm cần báo cáo.
     * @param thang Tháng cần báo cáo (1-12).
     * @return DTO chứa thông tin báo cáo tháng.
     */
    @Transactional(readOnly = true)
    public BaoCaoDoanhThuThangDTO getBaoCaoDoanhThuThang(int nam, int thang) {
        // Kiểm tra tính hợp lệ của tháng
        if (thang < 1 || thang > 12) {
            throw new IllegalArgumentException("Tháng không hợp lệ. Vui lòng nhập giá trị từ 1 đến 12.");
        }

        try {
            // Xác định ngày đầu và ngày cuối của tháng
            LocalDate startOfMonth = LocalDate.of(nam, thang, 1);
            LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());

            // Lấy tất cả hóa đơn
            List<HoaDon> allInvoices = hoaDonRepository.findAll();

            // Dùng TreeMap để tự động sắp xếp các tuần theo thứ tự
            Map<Integer, BigDecimal> weeklyTotals = new TreeMap<>();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());

            // Duyệt qua các hóa đơn để tính tổng doanh thu theo tuần
            for (HoaDon hd : allInvoices) {
                if ("Đã thanh toán".equals(hd.getTrangThai()) && hd.getThoiGianThanhCong() != null) {
                    LocalDate thanhCongDate = hd.getThoiGianThanhCong().toLocalDate();
                    // Chỉ xử lý các hóa đơn trong tháng đang xét
                    if (!thanhCongDate.isBefore(startOfMonth) && !thanhCongDate.isAfter(endOfMonth)) {
                        int weekOfYear = thanhCongDate.get(weekFields.weekOfWeekBasedYear());
                        weeklyTotals.merge(weekOfYear, hd.getTongTien(), BigDecimal::add);
                    }
                }
            }

            // Chuyển đổi Map kết quả sang định dạng có key mô tả (ví dụ: "Tuần 42 (14/10 - 20/10)")
            Map<String, BigDecimal> chiTietTheoTuan = new LinkedHashMap<>();
            for (Map.Entry<Integer, BigDecimal> entry : weeklyTotals.entrySet()) {
                String weekLabel = createWeekLabel(nam, entry.getKey(), startOfMonth, endOfMonth);
                chiTietTheoTuan.put(weekLabel, entry.getValue());
            }

            // Tính tổng doanh thu của cả tháng
            BigDecimal tongDoanhThu = chiTietTheoTuan.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

            // Xây dựng và trả về DTO
            return BaoCaoDoanhThuThangDTO.builder()
                    .nam(nam)
                    .thang(thang)
                    .tongDoanhThu(tongDoanhThu)
                    .chiTietTheoTuan(chiTietTheoTuan)
                    .build();

        } catch (Exception e) {
            throw new IllegalArgumentException("Năm hoặc tháng không hợp lệ.");
        }
    }

    /**
     * Phương thức hỗ trợ tạo nhãn cho tuần, ví dụ: "Tuần 42 (14/10 - 20/10)".
     * Nó sẽ cắt ngày bắt đầu/kết thúc của tuần để luôn nằm trong phạm vi của tháng.
     */
    private String createWeekLabel(int year, int weekOfYear, LocalDate startOfMonth, LocalDate endOfMonth) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        // Xác định một ngày bất kỳ trong tuần đó để tìm ngày đầu và cuối tuần
        LocalDate dayInWeek = LocalDate.of(year, startOfMonth.getMonthValue(), 1)
                                       .with(weekFields.weekOfWeekBasedYear(), weekOfYear);

        LocalDate startOfWeek = dayInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = dayInWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // Điều chỉnh ngày hiển thị để không vượt ra ngoài tháng
        LocalDate displayStart = startOfWeek.isBefore(startOfMonth) ? startOfMonth : startOfWeek;
        LocalDate displayEnd = endOfWeek.isAfter(endOfMonth) ? endOfMonth : endOfWeek;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        return String.format("Tuần %d (%s - %s)", weekOfYear, displayStart.format(formatter), displayEnd.format(formatter));
    }

    // ================================================================
    //  BÁO CÁO DOANH THU TUẦN
    // ================================================================
    
    @Transactional(readOnly = true)
    public BaoCaoDoanhThuTuanDTO getBaoCaoDoanhThuTuan(String ngay) {
        try {
            LocalDate inputDate = LocalDate.parse(ngay);

            // Tính toán ngày đầu và cuối tuần
            LocalDate startOfWeek = inputDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = inputDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            // Khởi tạo Map chi tiết với giá trị ban đầu là 0
            Map<String, BigDecimal> chiTietTheoNgay = new LinkedHashMap<>();
            chiTietTheoNgay.put("Thứ Hai", BigDecimal.ZERO);
            chiTietTheoNgay.put("Thứ Ba", BigDecimal.ZERO);
            chiTietTheoNgay.put("Thứ Tư", BigDecimal.ZERO);
            chiTietTheoNgay.put("Thứ Năm", BigDecimal.ZERO);
            chiTietTheoNgay.put("Thứ Sáu", BigDecimal.ZERO);
            chiTietTheoNgay.put("Thứ Bảy", BigDecimal.ZERO);
            chiTietTheoNgay.put("Chủ Nhật", BigDecimal.ZERO);
            
            // Lấy tất cả hóa đơn
            List<HoaDon> allInvoices = hoaDonRepository.findAll();

            // Sử dụng vòng lặp for để duyệt và tính toán
            for (HoaDon hd : allInvoices) {
                // Lọc các hóa đơn đã thanh toán và có ngày thanh toán
                if ("Đã thanh toán".equals(hd.getTrangThai()) && hd.getThoiGianThanhCong() != null) {
                    
                    // SỬA LỖI: Chuyển đổi từ LocalDateTime sang LocalDate một cách trực tiếp
                    LocalDate thanhCongDate = hd.getThoiGianThanhCong().toLocalDate();
                    
                    // Kiểm tra xem ngày thanh toán có nằm trong tuần đang xét không
                    if (!thanhCongDate.isBefore(startOfWeek) && !thanhCongDate.isAfter(endOfWeek)) {
                        DayOfWeek dayOfWeek = thanhCongDate.getDayOfWeek();
                        BigDecimal revenue = hd.getTongTien();

                        // Cập nhật doanh thu cho ngày tương ứng
                        switch (dayOfWeek) {
                            case MONDAY:
                                chiTietTheoNgay.merge("Thứ Hai", revenue, BigDecimal::add);
                                break;
                            case TUESDAY:
                                chiTietTheoNgay.merge("Thứ Ba", revenue, BigDecimal::add);
                                break;
                            case WEDNESDAY:
                                chiTietTheoNgay.merge("Thứ Tư", revenue, BigDecimal::add);
                                break;
                            case THURSDAY:
                                chiTietTheoNgay.merge("Thứ Năm", revenue, BigDecimal::add);
                                break;
                            case FRIDAY:
                                chiTietTheoNgay.merge("Thứ Sáu", revenue, BigDecimal::add);
                                break;
                            case SATURDAY:
                                chiTietTheoNgay.merge("Thứ Bảy", revenue, BigDecimal::add);
                                break;
                            case SUNDAY:
                                chiTietTheoNgay.merge("Chủ Nhật", revenue, BigDecimal::add);
                                break;
                        }
                    }
                }
            }

            // Tính tổng doanh thu từ map chi tiết
            BigDecimal tongDoanhThu = chiTietTheoNgay.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

            // Lấy thông tin năm và tuần
            int nam = inputDate.getYear();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int tuan = inputDate.get(weekFields.weekOfWeekBasedYear());

            // Xây dựng và trả về DTO cuối cùng
            return BaoCaoDoanhThuTuanDTO.builder()
                    .nam(nam)
                    .tuan(tuan)
                    .tongDoanhThu(tongDoanhThu)
                    .chiTietTheoNgay(chiTietTheoNgay)
                    .build();

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Định dạng ngày không hợp lệ. Vui lòng sử dụng định dạng 'YYYY-MM-DD'.");
        }
    }
}
