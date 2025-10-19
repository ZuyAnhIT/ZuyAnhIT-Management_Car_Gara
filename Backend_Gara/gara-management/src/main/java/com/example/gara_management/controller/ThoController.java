package com.example.gara_management.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;
import com.example.gara_management.service.ThoService;
import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.ThoDTO.ThoCreateDTO;
import com.example.gara_management.dto.ThoDTO.ThoResponseDTO;
import com.example.gara_management.dto.ThoDTO.ThoUpdateDTO;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Quản lý Thợ", description = "API thêm, sửa, xóa, xem thợ trong hệ thống gara")
@RestController
@RequestMapping("/api/")
@CrossOrigin(origins = "*")
public class ThoController {
    
    private final ThoService thoService;

    public ThoController(ThoService thoService){
        this.thoService = thoService;
    }

    @PostMapping("/them")
    public ResponseEntity<?> createTho(@Valid @RequestBody ThoCreateDTO createDTO){
        try {
            return new ResponseEntity(thoService.createTho(createDTO), HttpStatus.CREATED);
        } catch (ResourceAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
        } catch (Exception e){
            return new ResponseEntity<>("Lỗi hệ thống: "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/hienThiDanhSach")
    public ResponseEntity<PageResponseDTO<ThoResponseDTO>> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        return ResponseEntity.ok(thoService.getAllPaged(page, size, sortBy, sortDirection));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTho(@PathVariable Integer id, @RequestBody ThoUpdateDTO updateDTO) {
        try {
            return ResponseEntity.ok(thoService.updateTho(id, updateDTO));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ResourceAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi hệ thống: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDelete(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(thoService.softDeleteTho(id));
        } catch (ResourceNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (IllegalStateException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }
    }
    @GetMapping("/timKiem")
    public ResponseEntity<PageResponseDTO<ThoResponseDTO>> searchTho(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String tenTho,
            @RequestParam(required = false) String chuyenMon,
            @RequestParam(required = false) String trangThai) {
        return ResponseEntity.ok(thoService.searchTho(page, size, sortBy, sortDirection, tenTho, chuyenMon, trangThai));
    }

}   
