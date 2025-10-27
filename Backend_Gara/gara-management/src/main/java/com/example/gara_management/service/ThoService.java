package com.example.gara_management.service;

import com.example.gara_management.model.Tho;
import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.ThoDTO.ThoCreateDTO;
import com.example.gara_management.dto.ThoDTO.ThoResponseDTO;
import com.example.gara_management.dto.ThoDTO.ThoStatisticsDTO;
import com.example.gara_management.dto.ThoDTO.ThoUpdateDTO;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;
import com.example.gara_management.repository.ThoRepository;
import com.example.gara_management.util.JpaSpecificationUtil;
import com.example.gara_management.util.SortUtils;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThoService {
    
    private final ThoRepository thoRepository;

    public ThoService(ThoRepository thoRepository){
        this.thoRepository = thoRepository;
    }
    
    private String normalizePhoneNumber(String phone) {
        if (phone == null) return null;
        phone = phone.replaceAll("\\s+", "");
        if (phone.startsWith("+84")) {
            phone = "0" + phone.substring(3);
        } else if (phone.startsWith("84")) {
            phone = "0" + phone.substring(2);
        }
        return phone;
    }

    @Transactional
    public Tho createTho(ThoCreateDTO dto){
        
        // Chuẩn hóa số điện thoại
            String normalizedPhone = normalizePhoneNumber(dto.getSoDienThoai());

        // Kiểm tra độ dài và định dạng sau chuẩn hóa
            if (!normalizedPhone.matches("^0\\d{9}$")) {
                throw new IllegalArgumentException("Số điện thoại không hợp lệ sau chuẩn hóa (phải có 10 chữ số và bắt đầu bằng 0)");
            }
        thoRepository.findBySoDienThoai(dto.getSoDienThoai()).ifPresent(m -> {
            throw new ResourceAlreadyExistsException("Số điện thoại đã tồn tại: " + dto.getSoDienThoai());
        });
        thoRepository.findByEmail(dto.getEmail()).ifPresent(m -> {
            throw new ResourceAlreadyExistsException("Email đã tồn tại: " + dto.getEmail());
        });

        Tho tho = new Tho(
            null,
            dto.getTenTho(),
            dto.getChuyenMon(),
            normalizedPhone,
            dto.getEmail(),
            "Hoạt động",
            dto.getKinhNghiem(),
            java.time.LocalDateTime.now()
        );
        return thoRepository.save(tho);
    }

    public PageResponseDTO<ThoResponseDTO> getAllPaged(int page, int size, String sortBy, String sortDirection) {
    Sort sort = SortUtils.createSort(sortBy, sortDirection, "ngayVaoLam", Sort.Direction.DESC);
    Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
    Page<Tho> thoPage = thoRepository.findAll(pageable);
    List<ThoResponseDTO> dtos = thoPage.getContent()
        .stream().map(ThoResponseDTO::new).collect(Collectors.toList());
    return new PageResponseDTO<>(dtos, thoPage.getNumber(), thoPage.getSize(),
        thoPage.getTotalElements(), thoPage.getTotalPages(), thoPage.isLast());
    }

   @Transactional
    public Tho updateTho(Integer id, ThoUpdateDTO dto) {
        Tho tho = thoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thợ với mã: " + id));

        if (dto.getTenTho() != null) tho.setTenTho(dto.getTenTho());
        if (dto.getChuyenMon() != null) tho.setChuyenMon(dto.getChuyenMon());
        
        if (dto.getSoDienThoai() != null) {
            String normalizedPhone = normalizePhoneNumber(dto.getSoDienThoai());
            if (!normalizedPhone.matches("^0\\d{9}$")) {
                throw new IllegalArgumentException("Số điện thoại không hợp lệ sau chuẩn hóa (phải có 10 chữ số và bắt đầu bằng 0)");
            }
            // Kiểm tra trùng (trừ chính nó)
            thoRepository.findBySoDienThoai(normalizedPhone).ifPresent(existing -> {
                if (!existing.getMaTho().equals(id)) {
                    throw new ResourceAlreadyExistsException("Số điện thoại đã tồn tại: " + normalizedPhone);
                }
            });
            tho.setSoDienThoai(normalizedPhone);
        }
        if (dto.getEmail() != null) tho.setEmail(dto.getEmail());
        if (dto.getKinhNghiem() != null) tho.setKinhNghiem(dto.getKinhNghiem());

    if (dto.getTrangThai() == null || dto.getTrangThai().isBlank()) {
        tho.setTrangThai("Hoạt động");
    } else {
        tho.setTrangThai(dto.getTrangThai());
    }

        return thoRepository.save(tho);
    }
    @Transactional
    public Tho softDeleteTho(Integer id) {
        Tho tho = thoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thợ với mã: " + id));
        if ("Đã xóa".equals(tho.getTrangThai())) 
            throw new IllegalStateException("Thợ này đã bị xóa.");
        tho.setTrangThai("Đã xóa");
        return thoRepository.save(tho);
    }
    public PageResponseDTO<ThoResponseDTO> searchTho(
        int page, int size, String sortBy, String sortDirection,
        String tenTho, String chuyenMon, Integer kinhNghiem, 
        String trangThai, String soDienThoai) { // <-- ĐÃ THÊM: soDienThoai

    Specification<Tho> spec = Specification.where((Specification<Tho>) null)
        // Tìm kiếm theo Tên Thợ (LIKE)
        .and(JpaSpecificationUtil.<Tho>attributeContains("tenTho", tenTho))
        // Tìm kiếm theo Chuyên Môn (LIKE)
        .and(JpaSpecificationUtil.<Tho>attributeContains("chuyenMon", chuyenMon))
        // Tìm kiếm theo Số Điện Thoại (LIKE)
        .and(JpaSpecificationUtil.<Tho>attributeContains("soDienThoai", soDienThoai)) // <-- ĐÃ THÊM LOGIC TÌM KIẾM SĐT
        // Tìm kiếm theo Kinh Nghiệm (EQUAL)
        .and(JpaSpecificationUtil.<Tho>attributeEquals("kinhNghiem", kinhNghiem))
        // Tìm kiếm theo Trạng Thái (EQUAL)
        .and(JpaSpecificationUtil.<Tho>attributeEquals("trangThai", trangThai));

    Sort sort = SortUtils.createSort(sortBy, sortDirection, "ngayVaoLam", Sort.Direction.DESC);
    
    // Đảm bảo logic phân trang an toàn
    page = Math.max(0, page);
    size = Math.min(size, 100); 
    size = Math.max(1, size);
    
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<Tho> thoPage = thoRepository.findAll(spec, pageable);
    
    // Sửa lại cách tạo PageResponseDTO để sử dụng constructor chuẩn (nếu DTO có constructor Page)
    // Hoặc sử dụng cách bạn đã viết (chuyển đổi List)
    List<ThoResponseDTO> dtos = thoPage.getContent()
        .stream().map(ThoResponseDTO::new).collect(Collectors.toList());

    return new PageResponseDTO<>(dtos, thoPage.getNumber(), thoPage.getSize(),
            thoPage.getTotalElements(), thoPage.getTotalPages(), thoPage.isLast());
}

    @Transactional(readOnly = true)
public ThoStatisticsDTO getThoStatistics() {
    long totalTho = thoRepository.count(); // tổng số thợ
    long kinhNghiemCao = thoRepository.countByKinhNghiemGreaterThan(3);
    long kinhNghiemThap = thoRepository.countByKinhNghiemLessThanEqual(3);

    return new ThoStatisticsDTO(totalTho, kinhNghiemCao, kinhNghiemThap);
}




}
