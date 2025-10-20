package com.example.gara_management.controller;



import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.DichVuDTO.DichVuCreateDTO;
import com.example.gara_management.dto.DichVuDTO.DichVuResponseDTO;
import com.example.gara_management.dto.DichVuDTO.DichVuUpdateDTO;
import com.example.gara_management.model.DichVu;
import com.example.gara_management.service.DichVuService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Tag(name = "Quản lý Dịch Vụ", description = "API thêm, sửa, xóa, xem dịch vụ trong hệ thống gara")
@RestController
@RequestMapping("/api/dichvu") // Endpoint cho Dịch Vụ
// @SecurityRequirement(name = "bearerAuth")
public class DichVuController {

    private final DichVuService dichVuService;

    public DichVuController(DichVuService dichVuService) {
        this.dichVuService = dichVuService;
    }


    // API THÊM DỊCH VỤ
    /**
     * Endpoint POST để thêm dịch vụ mới.
     */
    // @PreAuthorize("hasAuthority('Quản lý')")
    @PostMapping("them")
    public ResponseEntity<?> createService(@Valid @RequestBody DichVuCreateDTO createDTO) {
        try {
            DichVu newService = dichVuService.addService(createDTO);
            
            // Trả về đối tượng vừa tạo với HTTP Status 201 Created
            return new ResponseEntity<>(newService, HttpStatus.CREATED); 
            
        } catch (ResourceNotFoundException e) {
            // Lỗi nghiệp vụ: Không tìm thấy Loại Dịch Vụ (404 Not Found)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            
        } catch (ResourceAlreadyExistsException e) {
            // Lỗi nghiệp vụ: Trùng tên Dịch Vụ (409 Conflict)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            
        } catch (Exception e) {
            // Lỗi khác (500 Internal Server Error)
            return new ResponseEntity<>("Lỗi hệ thống khi thêm dịch vụ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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

    
    //API CẬP NHẬT THÔNG TIN DỊCH VỤ
    // @PreAuthorize("hasAuthority('Quản lý')")
    @PutMapping("/{maDichVu}")
    public ResponseEntity<?> updateService(
            @PathVariable Integer maDichVu, 
            @Valid @RequestBody DichVuUpdateDTO updateDTO) {
        try {
            DichVu updatedService = dichVuService.updateService(maDichVu, updateDTO);
            
            return ResponseEntity.ok(updatedService);
            
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404
            
        } catch (ResourceAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409
            
        } catch (IllegalArgumentException e) { // Xử lý lỗi trạng thái/loại dịch vụ không hợp lệ
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request
            
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi hệ thống khi cập nhật dịch vụ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
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
}
