package com.example.gara_management.service;

import com.example.gara_management.dto.HoaDonDTO.HoaDonDTO;
import com.example.gara_management.dto.PhieuSuaChuaDTO.ChiTietPhieuSuaChuaDTO;
import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.model.HoaDon;
import com.example.gara_management.model.PhieuSuaChua; 
import com.example.gara_management.repository.HoaDonRepository;
import com.example.gara_management.repository.PhieuSuaChuaRepository; 
import com.example.gara_management.exception.*;
import com.example.gara_management.util.JpaSpecificationUtil;
import com.example.gara_management.util.SortUtils;
import org.springframework.data.domain.Pageable; 
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageRequest; 
import org.springframework.data.domain.Page;
import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HoaDonService {
    
    private final HoaDonRepository hoaDonRepository;
    private final PhieuSuaChuaRepository phieuSuaChuaRepository; // <-- Đã inject
    
    // --- LẤY THEO MÃ PHIẾU  ---
    public HoaDonDTO getHoaDonByMaPhieu(Integer maPhieu) {
        // Giả định mối quan hệ là LAZY, ta dùng phieuSuaChua.maPhieu
        HoaDon hoaDon = hoaDonRepository.findByPhieuSuaChua_MaPhieu(maPhieu)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn cho phiếu: " + maPhieu));
        return convertToDTO(hoaDon);
    }
    
    // --- 1. HIỂN THỊ DANH SÁCH (CÓ PHÂN TRANG & SẮP XẾP) ---
    /**
     * Lấy danh sách hóa đơn có phân trang và sắp xếp. Mặc định theo ngày lập hóa đơn (ngayLapHoaDon).
     */
    public PageResponseDTO<HoaDonDTO> getAllHoaDon(
            int page, int size, String sortBy, String sortDirection) {
        
        Sort sort = SortUtils.createSort(sortBy, sortDirection, "ngayLapHoaDon", Sort.Direction.DESC);
        page = Math.max(0, page);
        size = Math.min(size, 100); 
        size = Math.max(1, size);
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<HoaDon> hoaDonPage = hoaDonRepository.findAll(pageable);
        
        return new PageResponseDTO<>(hoaDonPage.map(this::convertToDTO));
    }

    // --- 2. TÌM KIẾM/LỌC (CÓ PHÂN TRANG & SẮP XẾP) ---
    /**
     * Tìm kiếm hóa đơn theo Mã phiếu, Tổng tiền (khoảng) và Trạng thái.
     */
    public PageResponseDTO<HoaDonDTO> searchHoaDon(
            int page, int size, String sortBy, String sortDirection,
            Integer maPhieu, BigDecimal tongTienMin, BigDecimal tongTienMax, String trangThai) {
        
        // 1. Xây dựng Specification
        Specification<HoaDon> spec = Specification.where(null);
        
        // Lọc theo Trạng thái (EQUAL)
        spec = spec.and(JpaSpecificationUtil.attributeEquals("trangThai", trangThai));

        // Lọc theo Mã Phiếu (JOIN)
        if (maPhieu != null) {
            // Giả định thuộc tính Entity trong HoaDon là "phieuSuaChua" và trường ID của nó là "maPhieu"
            spec = spec.and(JpaSpecificationUtil.attributeEquals("phieuSuaChua.maPhieu", maPhieu)); 
        }

        // Lọc theo Tổng Tiền (BETWEEN/Range)
        spec = spec.and(JpaSpecificationUtil.attributeBetween("tongTien", tongTienMin, tongTienMax));

        // 2. Xây dựng Pageable
        Sort sort = SortUtils.createSort(sortBy, sortDirection, "ngayLapHoaDon", Sort.Direction.DESC);
        page = Math.max(0, page);
        size = Math.min(size, 100); 
        size = Math.max(1, size);
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // 3. Gọi Repository
        Page<HoaDon> hoaDonPage = hoaDonRepository.findAll(spec, pageable);

        // 4. Chuyển đổi sang DTO
        return new PageResponseDTO<>(hoaDonPage.map(this::convertToDTO));
    }
    
    // -------------------------------------------------------------------
    // --- 3A. CẬP NHẬT TRẠNG THÁI (CHUYỂN ĐỔI NGHIỆP VỤ) ---
    // -------------------------------------------------------------------
    @Transactional
    public HoaDonDTO capNhatTrangThaiHoaDon(Integer maHoaDon, String trangThaiMoi) {
        
        HoaDon hoaDon = hoaDonRepository.findById(maHoaDon)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với mã: " + maHoaDon));
        
        // 1. Kiểm tra trạng thái hiện tại (Chỉ chấp nhận thay đổi khi "Chưa thanh toán")
        if (!"Chưa thanh toán".equalsIgnoreCase(hoaDon.getTrangThai())) {
            throw new IllegalStateException("Chỉ có thể thay đổi trạng thái hóa đơn khi ở trạng thái 'Chưa thanh toán'. Trạng thái hiện tại: " + hoaDon.getTrangThai());
        }
        
        PhieuSuaChua phieuSuaChua = hoaDon.getPhieuSuaChua();
        if (phieuSuaChua == null) {
             throw new ResourceNotFoundException("Hóa đơn không liên kết với Phiếu Sửa Chữa nào.");
        }
        
        String phieuTrangThai = phieuSuaChua.getTrangThai();
        
        // 2. Thực hiện chuyển đổi trạng thái
        if ("Đã thanh toán".equalsIgnoreCase(trangThaiMoi)) {
            // Cập nhật Hóa Đơn: Sử dụng kieuThanhToan mặc định (Tiền mặt) nếu chưa được đặt
            if (hoaDon.getKieuThanhToan() == null || hoaDon.getKieuThanhToan().isEmpty()) {
                hoaDon.setKieuThanhToan("Tiền mặt");
            }
            hoaDon.markAsPaid(); 
            
            // Cập nhật Phiếu Sửa Chữa
            phieuSuaChua.setTrangThai("Đã giao");
            phieuSuaChuaRepository.save(phieuSuaChua);

        } else if ("Đã hủy".equalsIgnoreCase(trangThaiMoi)) {
            // RÀNG BUỘC PHỨC TẠP: Chỉ hủy khi Phiếu ở trạng thái "Chờ xử lý"
            if (!"Chờ xử lý".equalsIgnoreCase(phieuTrangThai)) {
                throw new IllegalStateException("Không thể hủy hóa đơn vì Phiếu Sửa Chữa không ở trạng thái 'Chờ xử lý'. Trạng thái phiếu: " + phieuTrangThai);
            }
            
            // Cập nhật Hóa Đơn
            hoaDon.setTrangThai("Đã hủy");

            // Cập nhật Phiếu Sửa Chữa
            phieuSuaChua.setTrangThai("Hủy sửa");
            phieuSuaChuaRepository.save(phieuSuaChua);
            
        } else {
             throw new IllegalArgumentException("Trạng thái hóa đơn mới không hợp lệ. Chỉ chấp nhận 'Đã thanh toán' hoặc 'Đã hủy' từ trạng thái 'Chưa thanh toán'.");
        }
        
        HoaDon updated = hoaDonRepository.save(hoaDon);
        return convertToDTO(updated);
    }

    // -------------------------------------------------------------------
    // --- 3B. CẬP NHẬT KIỂU THANH TOÁN (TÁCH BIỆT) ---
    // -------------------------------------------------------------------
    /**
     * Cập nhật kiểu thanh toán. Chỉ cho phép khi hóa đơn ở trạng thái "Chưa thanh toán".
     */
    @Transactional
    public HoaDonDTO capNhatKieuThanhToan(Integer maHoaDon, String kieuThanhToanMoi) {
        
        HoaDon hoaDon = hoaDonRepository.findById(maHoaDon)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với mã: " + maHoaDon));
        
        // 1. Kiểm tra trạng thái (Chỉ chấp nhận khi "Chưa thanh toán")
        if (!"Chưa thanh toán".equalsIgnoreCase(hoaDon.getTrangThai())) {
            throw new IllegalStateException("Chỉ có thể cập nhật kiểu thanh toán khi hóa đơn ở trạng thái 'Chưa thanh toán'. Trạng thái hiện tại: " + hoaDon.getTrangThai());
        }
        
        // 2. Kiểm tra kiểu thanh toán hợp lệ (có thể thêm logic validation ở đây)
        if (kieuThanhToanMoi == null || kieuThanhToanMoi.trim().isEmpty()) {
             throw new IllegalArgumentException("Kiểu thanh toán không được để trống.");
        }

        // 3. Cập nhật
        hoaDon.setKieuThanhToan(kieuThanhToanMoi);
        
        HoaDon updated = hoaDonRepository.save(hoaDon);
        return convertToDTO(updated);
    }
    
    // --- HÀM CONVERT ĐÃ CẬP NHẬT ---
    private HoaDonDTO convertToDTO(HoaDon hoaDon) {
        
        List<ChiTietPhieuSuaChuaDTO> chiTietDTOs = null;
        
        // 1. Kiểm tra và tải Chi Tiết Phiếu Sửa Chữa
        PhieuSuaChua phieuSuaChua = hoaDon.getPhieuSuaChua();
        
        // Cần truy cập mối quan hệ để kích hoạt tải (Load)
        if (phieuSuaChua != null && phieuSuaChua.getChiTietList() != null) {
            
            // Tải Chi Tiết và chuyển đổi sang DTO
            chiTietDTOs = phieuSuaChua.getChiTietList().stream()
                .map(ct -> ChiTietPhieuSuaChuaDTO.builder()
                    // Giả định ChiTietPhieuSuaChuaDTO.java có các trường sau
                    .maDichVu(ct.getDichVu().getMaDichVu())
                    .tenDichVu(ct.getDichVu().getTenDichVu())
                    .soLuong(ct.getSoLuong())
                    .donGia(ct.getDonGia())
                    .thanhTien(ct.calculateThanhTien())
                    .build())
                .collect(Collectors.toList());
        }
        
        // 2. Xây dựng HoaDonDTO
        return HoaDonDTO.builder()
                .maHoaDon(hoaDon.getMaHoaDon())
                // Đảm bảo getPhieuSuaChua không null trước khi gọi getMaPhieu()
                .maPhieu(phieuSuaChua != null ? phieuSuaChua.getMaPhieu() : null)
                .ngayLapHoaDon(hoaDon.getNgayLapHoaDon())
                .thoiGianThanhCong(hoaDon.getThoiGianThanhCong())
                .kieuThanhToan(hoaDon.getKieuThanhToan())
                .trangThai(hoaDon.getTrangThai())
                .tongTien(hoaDon.getTongTien())
                .chiTietList(chiTietDTOs) // <-- GÁN CHI TIẾT DTO VÀO ĐÂY
                .build();
    }
}