package com.example.gara_management.service;

import com.example.gara_management.model.Tho;
import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.ThoDTO.ThoCreateDTO;
import com.example.gara_management.dto.ThoDTO.ThoResponseDTO;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
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



}
