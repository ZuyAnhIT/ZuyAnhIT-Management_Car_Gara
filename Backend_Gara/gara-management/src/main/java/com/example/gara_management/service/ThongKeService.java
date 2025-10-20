package com.example.gara_management.service;

import com.example.gara_management.dto.BaoCaoThongKeDTO.ThongKeDTO;
import com.example.gara_management.repository.DichVuRepository;
import com.example.gara_management.repository.HoaDonRepository;
import com.example.gara_management.repository.KhachHangRepository;
import com.example.gara_management.repository.LoaiDichVuRepository;
import com.example.gara_management.repository.ThoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}