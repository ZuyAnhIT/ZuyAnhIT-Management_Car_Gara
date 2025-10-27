package com.example.gara_management.controller;



import com.example.gara_management.dto.ApiResponse;
import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.DichVuDTO.DichVuCreateDTO;
import com.example.gara_management.dto.DichVuDTO.DichVuResponseDTO;
import com.example.gara_management.dto.DichVuDTO.DichVuStatisticsDTO;
import com.example.gara_management.dto.DichVuDTO.DichVuUpdateDTO;
import com.example.gara_management.model.DichVu;
import com.example.gara_management.service.DichVuService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.MediaType;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;


@Tag(name = "Quản lý Dịch Vụ", description = "API thêm, sửa, xóa, xem dịch vụ trong hệ thống gara")
@RestController
@RequestMapping("/api/dichvu") // Endpoint cho Dịch Vụ
@CrossOrigin(origins = "*")
public class DichVuController {

    private final DichVuService dichVuService;

    public DichVuController(DichVuService dichVuService) {
        this.dichVuService = dichVuService;
    }

    // ====================================================================
    // 1. API THÊM DỊCH VỤ (POST - MULTIPART)
    // ====================================================================
    //@PreAuthorize("hasAuthority('Quản lý')") 
    @PostMapping(value = "/them", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DichVuResponseDTO>> createService(
            @Valid @ModelAttribute DichVuCreateDTO createDTO,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                    .orElse("Dữ liệu không hợp lệ");
            return ResponseEntity.badRequest().body(ApiResponse.error(errorMessages));
        }

        try {
            DichVu result = dichVuService.addService(createDTO, imageFile);
            DichVuResponseDTO responseDTO = new DichVuResponseDTO(result);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Thêm dịch vụ thành công", responseDTO));

        } catch (ResourceAlreadyExistsException | ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Lỗi nghiệp vụ: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi thêm dịch vụ: " + e.getMessage()));
        }
    }

    // ====================================================================
    // 2. API CẬP NHẬT THÔNG TIN DỊCH VỤ (PUT - MULTIPART)
    // ====================================================================
    //@PreAuthorize("hasAuthority('Quản lý')")
    @PutMapping(value = "/{maDichVu}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DichVuResponseDTO>> updateService(
            @PathVariable Integer maDichVu,
            @Valid @ModelAttribute DichVuUpdateDTO updateDTO,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                    .orElse("Dữ liệu không hợp lệ");
            return ResponseEntity.badRequest().body(ApiResponse.error(errorMessages));
        }

        try {
            DichVu updatedEntity = dichVuService.updateService(maDichVu, updateDTO, imageFile);
            DichVuResponseDTO responseDTO = new DichVuResponseDTO(updatedEntity);

            return ResponseEntity.ok(ApiResponse.success("Cập nhật dịch vụ thành công", responseDTO));

        } catch (ResourceNotFoundException | ResourceAlreadyExistsException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống khi cập nhật dịch vụ: " + e.getMessage()));
        }
    }

    //// API  HIỂN THỊ DANH SÁCH & SẮP XẾP 
    /**
     * Endpoint GET để lấy danh sách dịch vụ (không có tìm kiếm/lọc).
     * URL ví dụ: /api/dichvu?sortBy=tenDichVu&sortDirection=asc
     */
    // @PreAuthorize("hasAuthority('Quản lý')") // <-- Yêu cầu quyền: hasAuthority là an toàn nhất
    @GetMapping("hienThiDanhSach")
    public ResponseEntity<PageResponseDTO<DichVuResponseDTO>> getAllServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy, 
            @RequestParam(required = false) String sortDirection) { 
        
        PageResponseDTO<DichVuResponseDTO> responseDTO = 
                dichVuService.getAllServices(page, size, sortBy, sortDirection);
        
        return ResponseEntity.ok(responseDTO);
    }

    // API  TÌM KIẾM & PHÂN TRANG & SẮP XẾP ---
    /**
     * Endpoint GET riêng để tìm kiếm dịch vụ theo các tiêu chí khác nhau.
     * URL ví dụ: /api/dichvu/search?tenDichVu=Thay dầu&trangThai=Hết hàng
     */
    // @PreAuthorize("hasAuthority('Quản lý')")
    @GetMapping("/timKiem") // <-- API riêng biệt cho tìm kiếm
    public ResponseEntity<PageResponseDTO<DichVuResponseDTO>> searchServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy, 
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String tenDichVu, 
            @RequestParam(required = false) String tenLoai) { 
        
        PageResponseDTO<DichVuResponseDTO> responseDTO = 
                dichVuService.searchServices(page, size, sortBy, sortDirection, tenDichVu, tenLoai);
        
        return ResponseEntity.ok(responseDTO);
    }

    //API XÓA MỀM DỊCH VỤ
    /**
     * Endpoint DELETE để thực hiện xóa mềm (Soft Delete) dịch vụ.
     * @param maDichVu Mã dịch vụ cần xóa.
     * @return ResponseEntity chứa đối tượng đã xóa mềm hoặc thông báo lỗi.
     */
    // @PreAuthorize("hasAuthority('Quản lý')")
    @DeleteMapping("/{maDichVu}")
    public ResponseEntity<?> softDeleteService(@PathVariable Integer maDichVu) {
        try {
            // Gọi Service để xóa mềm
            DichVu deletedService = dichVuService.softDeleteService(maDichVu);
            
            // Trả về đối tượng vừa xóa mềm với HTTP Status 200 OK
            return ResponseEntity.ok(deletedService);
            
        } catch (ResourceNotFoundException e) {
            // Lỗi không tìm thấy (404 Not Found)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            
        } catch (IllegalStateException e) {
            // Lỗi nghiệp vụ đã bị xóa (409 Conflict)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            
        } catch (Exception e) {
            // Lỗi khác (500 Internal Server Error)
            return new ResponseEntity<>("Lỗi hệ thống khi xóa mềm dịch vụ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/thongKe")
public ResponseEntity<?> getDichVuStatistics() {
    try {
        DichVuStatisticsDTO stats = dichVuService.getDichVuStatistics();
        return ResponseEntity.ok(stats);
    } catch (Exception e) {
        return new ResponseEntity<>("Lỗi hệ thống: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

}
