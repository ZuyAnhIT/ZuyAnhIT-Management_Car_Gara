package com.example.gara_management.service;

import com.example.gara_management.model.Tho;
import com.example.gara_management.dto.ThoDTO.ThoCreateDTO;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.repository.ThoRepository;
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
}
