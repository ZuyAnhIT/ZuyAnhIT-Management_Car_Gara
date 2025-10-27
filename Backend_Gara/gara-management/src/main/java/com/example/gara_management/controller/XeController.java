package com.example.gara_management.controller;

import com.example.gara_management.dto.ApiResponse;
import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.XeDTO.XeCreateDTO;
import com.example.gara_management.dto.XeDTO.XeResponseDTO;
import com.example.gara_management.dto.XeDTO.XeUpdateDTO;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;
import com.example.gara_management.model.Xe;
import com.example.gara_management.service.XeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Quản lý Xe", description = "API hiển thị và tìm kiếm xe trong hệ thống gara")
@RestController
@RequestMapping("/api/xe")
@CrossOrigin(origins = "*")
public class XeController {

    private final XeService xeService;

    public XeController(XeService xeService) {
        this.xeService = xeService;
    }

    // ==========================================================
    //  HIỂN THỊ DANH SÁCH XE
    // ==========================================================
    @GetMapping("/hienThiDanhSach")
    public ResponseEntity<PageResponseDTO<XeResponseDTO>> getAllXe(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {

        PageResponseDTO<XeResponseDTO> responseDTO =
                xeService.getAllXe(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(responseDTO);
    }

    // ==========================================================
    //  TÌM KIẾM XE
    // ==========================================================
    @GetMapping("/timKiem")
    public ResponseEntity<ApiResponse<PageResponseDTO<XeResponseDTO>>> searchXe(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "maXe") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String bienSo,
            @RequestParam(required = false) String hangXe,
            @RequestParam(required = false) Integer namSanXuat,
            @RequestParam(required = false) String mauSac,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String tenKhachHang) {

        try {
            PageResponseDTO<XeResponseDTO> responseDTO = xeService.searchXe(
                    page, size, sortBy, sortDirection,
                    bienSo, hangXe, namSanXuat, mauSac, trangThai, tenKhachHang
            );

            return ResponseEntity.ok(ApiResponse.success("Tìm kiếm xe thành công", responseDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống khi tìm kiếm xe: " + e.getMessage()));
        }
    }

    // ==========================================================
    //  THÊM XE
    // ==========================================================
    @PostMapping("/them")
    public ResponseEntity<?> createXe(
            @Valid @RequestBody XeCreateDTO createDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                    .orElse("Dữ liệu không hợp lệ");
            return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
        }

        try {
            Xe newXe = xeService.createXe(createDTO);
            return new ResponseEntity<>(newXe, HttpStatus.CREATED);

        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (ResourceAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);

        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi hệ thống khi thêm xe: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ==========================================================
    //  CẬP NHẬT XE
    // ==========================================================
    @PutMapping("/{maXe}")
    public ResponseEntity<?> updateXe(
            @PathVariable Integer maXe,
            @Valid @RequestBody XeUpdateDTO updateDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                    .orElse("Dữ liệu không hợp lệ");
            return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
        }

        try {
            Xe updatedXe = xeService.updateXe(maXe, updateDTO);
            return ResponseEntity.ok(updatedXe);

        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (ResourceAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi hệ thống khi cập nhật xe: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ==========================================================
    //  XÓA MỀM XE
    // ==========================================================
    @DeleteMapping("/{maXe}")
    public ResponseEntity<?> softDeleteXe(@PathVariable Integer maXe) {
        try {
            Xe deletedXe = xeService.softDeleteXe(maXe);
            return ResponseEntity.ok(deletedXe);

        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);

        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi hệ thống khi xóa mềm xe: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ==========================================================
    //  THỐNG KÊ XE
    // ==========================================================
    @GetMapping("/thongKeXe")
    public ResponseEntity<Map<String, Long>> thongKeXe() {
        Map<String, Long> data = xeService.thongKeXe();
        return ResponseEntity.ok(data);
    }
}
