package com.example.gara_management.service;

import com.example.gara_management.dto.BaoCaoDTO.TongDoanhThuDTO;
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
}
