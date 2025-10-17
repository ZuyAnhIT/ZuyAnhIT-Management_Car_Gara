package com.example.gara_management.service;

import com.example.gara_management.model.Tho;
import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.ThoDTO.ThoCreateDTO;
import com.example.gara_management.dto.ThoDTO.ThoResponseDTO;
import com.example.gara_management.dto.ThoDTO.ThoUpdateDTO;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;
import com.example.gara_management.repository.ThoRepository;
import com.example.gara_management.util.SortUtils;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThoService {
    
    private final ThoRepository thoRepository;

    public ThoService(ThoRepository thoRepository){
        this.thoRepository = thoRepository;
    }

    @Transactional
    public Tho createTho(ThoCreateDTO dto){
        thoRepository.findByEmail(dto.getEmail()).ifPresent(m -> {
            throw new ResourceAlreadyExistsException("Email đã tồn tại: " + dto.getEmail());
        });
     thoRepository.findBySoDienThoai(dto.getSoDienThoai()).ifPresent(m -> {
            throw new ResourceAlreadyExistsException("Số điện thoại đã tồn tại: " + dto.getSoDienThoai());
        });

        Tho tho = new Tho(
            null,
            dto.getTenTho(),
            dto.getChuyenMon(),
            dto.getSoDienThoai(),
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
        if (dto.getSoDienThoai() != null) tho.setSoDienThoai(dto.getSoDienThoai());
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




}
