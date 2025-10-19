package com.example.gara_management.repository;

import com.example.gara_management.model.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BaoCaoRepository extends JpaRepository<HoaDon, Integer> {
    
    /**
     * Tính tổng doanh thu từ tất cả các hóa đơn có trạng thái "Đã thanh toán"
     * @return BigDecimal - Tổng doanh thu
     */
    @Query("SELECT COALESCE(SUM(h.tongTien), 0) FROM HoaDon h WHERE h.trangThai = 'Đã thanh toán'")
    BigDecimal tinhTongDoanhThu();
    
    /**
     * Tính tổng số lượng tồn kho từ tất cả các dịch vụ
     * @return Long - Tổng số lượng tồn kho
     */
    @Query("SELECT COALESCE(SUM(d.soLuongTon), 0) FROM DichVu d")
    Long tinhTongSoLuongTon();
    
    /**
     * Tính tổng số lượng khách hàng từ bảng KhachHang
     * @return Long - Tổng số lượng khách hàng
     */
    @Query("SELECT COUNT(k) FROM KhachHang k")
    Long tinhTongSoLuongKhachHang();
    
    /**
     * Tính tổng doanh thu trong tuần của ngày được chỉ định
     * @param startOfWeek Ngày đầu tuần (Thứ 2)
     * @param endOfWeek Ngày cuối tuần (Chủ nhật)
     * @return BigDecimal - Tổng doanh thu trong tuần
     */
    @Query("SELECT COALESCE(SUM(h.tongTien), 0) FROM HoaDon h WHERE h.trangThai = 'Đã thanh toán' " +
           "AND h.thoiGianThanhCong BETWEEN :startOfWeek AND :endOfWeek")
    BigDecimal tinhTongDoanhThuTheoTuan(@Param("startOfWeek") LocalDateTime startOfWeek, @Param("endOfWeek") LocalDateTime endOfWeek);
    
    /**
     * Lấy doanh thu theo từng ngày trong tuần
     * @param startOfWeek Ngày đầu tuần (Thứ 2)
     * @param endOfWeek Ngày cuối tuần (Chủ nhật)
     * @return List<Object[]> - Mỗi phần tử chứa [ngay, doanhThu, soHoaDon]
     */
    @Query("SELECT DATE(h.thoiGianThanhCong) as ngay, " +
           "COALESCE(SUM(h.tongTien), 0) as doanhThu, " +
           "COUNT(h) as soHoaDon " +
           "FROM HoaDon h " +
           "WHERE h.trangThai = 'Đã thanh toán' " +
           "AND h.thoiGianThanhCong BETWEEN :startOfWeek AND :endOfWeek " +
           "GROUP BY DATE(h.thoiGianThanhCong) " +
           "ORDER BY DATE(h.thoiGianThanhCong)")
    List<Object[]> layDoanhThuTheoNgayTrongTuan(@Param("startOfWeek") LocalDateTime startOfWeek, @Param("endOfWeek") LocalDateTime endOfWeek);
}