package com.example.gara_management.repository;

import com.example.gara_management.model.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

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
}