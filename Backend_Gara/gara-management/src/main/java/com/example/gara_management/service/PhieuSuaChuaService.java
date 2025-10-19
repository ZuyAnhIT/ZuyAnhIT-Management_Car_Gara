package com.example.gara_management.service;

import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.PhieuSuaChuaDTO.ChiTietPhieuSuaChuaDTO;
import com.example.gara_management.dto.PhieuSuaChuaDTO.CreatePhieuSuaChuaRequest;
import com.example.gara_management.dto.PhieuSuaChuaDTO.PhieuSuaChuaDTO;
import com.example.gara_management.exception.ResourceNotFoundException;
import com.example.gara_management.model.*;
import com.example.gara_management.repository.*;
import com.example.gara_management.util.JpaSpecificationUtil;
import com.example.gara_management.util.SortUtils;
import org.springframework.data.domain.Pageable; 
import org.springframework.data.domain.Sort;     
import org.springframework.data.domain.PageRequest; 
import org.springframework.data.domain.Page;
import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhieuSuaChuaService {
    
    private final PhieuSuaChuaRepository phieuSuaChuaRepository;
    private final ChiTietPhieuSuaChuaRepository chiTietRepository;
    private final HoaDonRepository hoaDonRepository;
    private final XeRepository xeRepository;
    private final ThoRepository thoRepository;
    private final DichVuRepository dichVuRepository;
    
    // --- CHỨC NĂNG 1: TẠO MỚI PHIẾU SỬA CHỮA (Đã sửa Exceptions) ---
    @Transactional
    public PhieuSuaChuaDTO createPhieuSuaChua(CreatePhieuSuaChuaRequest request) {
        
        // 1. Validate Xe (Sửa RuntimeException -> ResourceNotFoundException)
        Xe xe = xeRepository.findById(request.getMaXe())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với mã: " + request.getMaXe()));
        
        // 2. Validate Tho (Sửa RuntimeException -> ResourceNotFoundException)
        Tho tho = thoRepository.findById(request.getMaTho())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thợ với mã: " + request.getMaTho()));
        
        // 3. Tạo phiếu sửa chữa (Giữ nguyên logic tạo phiếu)
        PhieuSuaChua phieuSuaChua = PhieuSuaChua.builder()
                .xe(xe)
                .tho(tho)
                .moTa(request.getMoTa())
                .tongTien(BigDecimal.ZERO)
                .build();
        
        List<ChiTietPhieuSuaChua> chiTietList = new ArrayList<>();
        BigDecimal tongTien = BigDecimal.ZERO;
        
        for (CreatePhieuSuaChuaRequest.ChiTietRequest chiTietReq : request.getChiTietList()) {
            // Lấy thông tin dịch vụ
            DichVu dichVu = dichVuRepository.findById(chiTietReq.getMaDichVu())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ với mã: " + chiTietReq.getMaDichVu()));
            
            // ... (Giữ nguyên logic tạo chi tiết, tính toán tổng tiền, và cập nhật số lượng tồn)
            
            ChiTietPhieuSuaChua chiTiet = ChiTietPhieuSuaChua.builder()
                    .phieuSuaChua(phieuSuaChua)
                    .dichVu(dichVu)
                    .soLuong(chiTietReq.getSoLuong())
                    .donGia(dichVu.getGia()) // Lấy giá từ dịch vụ
                    .build();
            
            chiTietList.add(chiTiet);
            
            // Tính tổng tiền
            BigDecimal thanhTien = dichVu.getGia().multiply(BigDecimal.valueOf(chiTietReq.getSoLuong()));
            tongTien = tongTien.add(thanhTien);
            
            // Cập nhật số lượng bán/tồn và trạng thái (Giữ nguyên logic của bạn)
            dichVu.setSoLuongBan(dichVu.getSoLuongBan() + chiTietReq.getSoLuong());
            if (dichVu.getSoLuongTon() > 0) {
                 dichVu.setSoLuongTon(dichVu.getSoLuongTon() - chiTietReq.getSoLuong());
                 if (dichVu.getSoLuongTon() <= 0) {
                     dichVu.setTrangThai("Hết hàng");
                 }
            }
            // Lưu lại dịch vụ đã cập nhật (Rất quan trọng trong cùng transaction)
            dichVuRepository.save(dichVu); 
        }
        
        phieuSuaChua.setTongTien(tongTien);
        phieuSuaChua.setChiTietList(chiTietList);
        
        // 5. Lưu phiếu sửa chữa
        PhieuSuaChua savedPhieu = phieuSuaChuaRepository.save(phieuSuaChua);
        
        // 6. Tự động tạo hóa đơn (Giữ nguyên logic của bạn)
        HoaDon hoaDon = HoaDon.builder()
                .phieuSuaChua(savedPhieu)
                .tongTien(tongTien)
                .build();
        
        hoaDonRepository.save(hoaDon);
        
        // 7. Chuyển đổi sang DTO
        return convertToDTO(savedPhieu);
    }
    
    // --- CHỨC NĂNG 2: HIỂN THỊ DANH SÁCH (Sử dụng SortUtils) ---
    public PageResponseDTO<PhieuSuaChuaDTO> getAllPhieuSuaChua(
            int page, int size, String sortBy, String sortDirection) {
        
        Sort sort = SortUtils.createSort(sortBy, sortDirection, "ngayLap", Sort.Direction.DESC);
        page = Math.max(0, page);
        size = Math.min(size, 100); 
        size = Math.max(1, size);
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PhieuSuaChua> phieuSuaChuaPage = phieuSuaChuaRepository.findAll(pageable); // Đảm bảo Repository có JpaSpecificationExecutor nếu cần
        
        // Chuyển đổi Page<Entity> sang Page<DTO>
        return new PageResponseDTO<>(phieuSuaChuaPage.map(this::convertToDTO));
    }
    
    // --- CHỨC NĂNG 3: TÌM KIẾM/LỌC (Sử dụng JpaSpecificationUtil) ---
    public PageResponseDTO<PhieuSuaChuaDTO> searchPhieuSuaChua(
            int page, int size, String sortBy, String sortDirection,
            String trangThai, String bienSo) {

        // 1. Xây dựng Specification
        Specification<PhieuSuaChua> spec = Specification.where(null); 
        
        // Lọc theo trạng thái (EQUAL)
        spec = spec.and(JpaSpecificationUtil.attributeEquals("trangThai", trangThai));
        
        // Tìm kiếm theo Biển Số Xe (JOIN và LIKE)
        if (bienSo != null && !bienSo.trim().isEmpty()) {
            spec = spec.and(JpaSpecificationUtil.attributeContainsJoin("xe", "bienSo", bienSo));
        }
        

        // 2. Xây dựng Pageable
        Sort sort = SortUtils.createSort(sortBy, sortDirection, "ngayLap", Sort.Direction.DESC);
        page = Math.max(0, page);
        size = Math.min(size, 100); 
        size = Math.max(1, size);
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // 3. Gọi Repository (sử dụng Specification)
        Page<PhieuSuaChua> phieuSuaChuaPage = phieuSuaChuaRepository.findAll(spec, pageable);

        // 4. Chuyển đổi sang DTO
        return new PageResponseDTO<>(phieuSuaChuaPage.map(this::convertToDTO));
    }


    /**
     * Cập nhật trạng thái phiếu sửa chữa, chỉ cho phép nếu trạng thái KHÔNG phải là "Đã giao".
     * @param maPhieu Mã phiếu cần cập nhật.
     * @param trangThai Trạng thái mới.
     * @return PhieuSuaChuaDTO đã cập nhật.
     * @throws ResourceNotFoundException Nếu không tìm thấy phiếu.
     * @throws IllegalStateException Nếu phiếu đã ở trạng thái "Đã giao".
     */
    @Transactional
    public PhieuSuaChuaDTO updateTrangThai(Integer maPhieu, String trangThai) {
        
        // 1. Tìm phiếu sửa chữa
        PhieuSuaChua phieuSuaChua = phieuSuaChuaRepository.findById(maPhieu)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu sửa chữa với mã: " + maPhieu));
        
        // 2. RÀNG BUỘC NGHIỆP VỤ: KHÔNG CHO PHÉP SỬA NẾU ĐÃ "Đã giao"
        if ("Đã giao".equalsIgnoreCase(phieuSuaChua.getTrangThai())) {
            throw new IllegalStateException("Phiếu sửa chữa đã ở trạng thái 'Đã giao' và không thể cập nhật trạng thái nữa.");
        }
        

        // 3. Cập nhật trạng thái
        phieuSuaChua.setTrangThai(trangThai);
        PhieuSuaChua updated = phieuSuaChuaRepository.save(phieuSuaChua);
        
        // 4. Chuyển đổi và trả về
        return convertToDTO(updated);
    }

    // --- HÀM CONVERT ---
    private PhieuSuaChuaDTO convertToDTO(PhieuSuaChua phieuSuaChua) {
        List<ChiTietPhieuSuaChuaDTO> chiTietDTOs = new ArrayList<>();
        
        if (phieuSuaChua.getChiTietList() != null) {
             chiTietDTOs = phieuSuaChua.getChiTietList().stream()
                     .map(ct -> ChiTietPhieuSuaChuaDTO.builder()
                             .maDichVu(ct.getDichVu().getMaDichVu())
                             .tenDichVu(ct.getDichVu().getTenDichVu())
                             .soLuong(ct.getSoLuong())
                             .donGia(ct.getDonGia())
                             .thanhTien(ct.calculateThanhTien())
                             .build())
                     .collect(Collectors.toList());
        }
        
        return PhieuSuaChuaDTO.builder()
                .maPhieu(phieuSuaChua.getMaPhieu())
                .maXe(phieuSuaChua.getXe().getMaXe())
                .bienSo(phieuSuaChua.getXe().getBienSo()) // Lấy Biển số xe
                .maTho(phieuSuaChua.getTho().getMaTho())
                .tenTho(phieuSuaChua.getTho().getTenTho()) // Lấy Tên Thợ
                .ngayLap(phieuSuaChua.getNgayLap())
                .moTa(phieuSuaChua.getMoTa())
                .trangThai(phieuSuaChua.getTrangThai())
                .tongTien(phieuSuaChua.getTongTien())
                .chiTietList(chiTietDTOs)
                .build();
    }
}

