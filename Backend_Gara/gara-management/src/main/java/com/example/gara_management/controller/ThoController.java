package com.example.gara_management.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.service.ThoService;
import com.example.gara_management.dto.ThoDTO.ThoCreateDTO;


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
}   
