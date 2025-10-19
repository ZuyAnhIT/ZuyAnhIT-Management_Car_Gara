package com.example.gara_management.service;

import com.example.gara_management.dto.BaoCaoDTO.TongDoanhThuDTO;
import com.example.gara_management.dto.BaoCaoDTO.TongTonKhoDTO;
import com.example.gara_management.repository.BaoCaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

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
     * @return TongTonKhoDTO chứa tổng số lượng khách hàng (tái sử dụng DTO)
     */
    public TongTonKhoDTO layTongSoLuongKhachHang() {
        try {
            // Lấy tổng số lượng khách hàng từ database
            Long tongSoLuongKhachHang = baoCaoRepository.tinhTongSoLuongKhachHang();
            
            // Kiểm tra nếu không có dữ liệu
            if (tongSoLuongKhachHang == 0) {
                return TongTonKhoDTO.builder()
                        .tongSoLuongTon(0L)
                        .message("Không có khách hàng nào trong hệ thống")
                        .build();
            }
            
            // Trả về kết quả thành công
            return TongTonKhoDTO.builder()
                    .tongSoLuongTon(tongSoLuongKhachHang)
                    .message("Lấy tổng số lượng khách hàng thành công")
                    .build();
            
        } catch (Exception e) {
            // Xử lý lỗi và trả về response lỗi
            return TongTonKhoDTO.builder()
                    .tongSoLuongTon(0L)
                    .message("Lỗi khi lấy tổng số lượng khách hàng: " + e.getMessage())
                    .build();
        }
    }
}