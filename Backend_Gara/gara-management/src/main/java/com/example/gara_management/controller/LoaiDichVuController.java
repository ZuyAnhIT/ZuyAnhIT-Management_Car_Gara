package com.example.gara_management.controller;

import com.example.gara_management.model.LoaiDichVu;
import com.example.gara_management.service.LoaiDichVuService;
import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.LoaiDichVuDTO.LoaiDichVuCreateDTO;
import com.example.gara_management.dto.LoaiDichVuDTO.LoaiDichVuResponseDTO;
import com.example.gara_management.dto.LoaiDichVuDTO.LoaiDichVuUpdateDTO;
import com.example.gara_management.exception.ResourceAlreadyExistsException;
import com.example.gara_management.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid; // Thêm import này
@Tag(name = "Quản lý Loại Dịch Vụ", description = "API thêm, sửa, xóa, xem loại dịch vụ trong hệ thống gara")
@RestController
@RequestMapping("/api/loaidichvu")
@CrossOrigin(origins = "*")
public class LoaiDichVuController {

    private final LoaiDichVuService loaiDichVuService;

    // Dependency Injection qua Constructor
    public LoaiDichVuController(LoaiDichVuService loaiDichVuService) {
        this.loaiDichVuService = loaiDichVuService;
    }


    // API THÊM MỚI LOẠI DỊCH VỤ
    /**
     * Endpoint POST để thêm loại dịch vụ mới.
     * @param createDTO Dữ liệu đầu vào từ body request, được validated.
     * @return ResponseEntity chứa đối tượng LoaiDichVu đã tạo hoặc thông báo lỗi.
     */
    @PostMapping("/them")
    public ResponseEntity<?> createLoaiDichVu(@Valid @RequestBody LoaiDichVuCreateDTO createDTO) {
        try {
            // Gọi Service để thực hiện nghiệp vụ
            LoaiDichVu newLoaiDichVu = loaiDichVuService.themLoaiDichVu(createDTO);
            
            // Trả về đối tượng vừa tạo với HTTP Status 201 Created
            return new ResponseEntity<>(newLoaiDichVu, HttpStatus.CREATED); 
            
        } catch (ResourceAlreadyExistsException e) {
            // Xử lý lỗi nghiệp vụ (ví dụ: trùng tên) với HTTP Status 409 Conflict
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            
        } catch (Exception e) {
            // Xử lý các lỗi khác (ví dụ: lỗi DB, server) với HTTP Status 500 Internal Server Error
            // Nên ghi log lỗi chi tiết ở đây
            return new ResponseEntity<>("Lỗi hệ thống không xác định: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //API HIỂN THỊ DANH SÁCH LOẠI DỊCH VỤ
     /**
     * Endpoint GET để lấy danh sách loại dịch vụ có phân trang và sắp xếp tùy chỉnh.
     * Mặc định: sắp xếp theo ngayTao (desc).
     * @param page Số trang (mặc định 0)
     * @param size Kích thước trang (mặc định 10)
     * @param sortBy Trường để sắp xếp.
     * @param sortDirection Hướng sắp xếp.
     */
    @GetMapping("hienThiDanhSach")
    public ResponseEntity<PageResponseDTO<LoaiDichVuResponseDTO>> getAllServiceTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            // Đặt giá trị mặc định cho sortBy và sortDirection
            @RequestParam(required = false) String sortBy, 
            @RequestParam(required = false) String sortDirection) { 
        
        // Gọi Service với các tham số, Service sẽ tự động dùng mặc định nếu các tham số này null/empty
        PageResponseDTO<LoaiDichVuResponseDTO> responseDTO = 
                loaiDichVuService.getAllServiceTypes(page, size, sortBy, sortDirection);
        
        return ResponseEntity.ok(responseDTO);
    }
    
    // API TÌM KIẾM & PHÂN TRANG & SẮP XẾP (API tái sử dụng lại cả hiển thị)
    /**
     * Endpoint GET riêng để tìm kiếm loại dịch vụ theo các tiêu chí khác nhau.
     * URL ví dụ: /api/loaidichvu/search?tenLoai=kiểm tra&trangThai=Hoạt động
     */
    @GetMapping("/timKiem") // <-- API riêng biệt
    public ResponseEntity<PageResponseDTO<LoaiDichVuResponseDTO>> searchServiceTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy, 
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String tenLoai, 
            @RequestParam(required = false) String trangThai) { 
        
        // Gọi Service
        PageResponseDTO<LoaiDichVuResponseDTO> responseDTO = 
                loaiDichVuService.searchServiceTypes(page, size, sortBy, sortDirection, tenLoai, trangThai); // Gọi hàm searchServiceTypes
        
        return ResponseEntity.ok(responseDTO);
    }

    //API CẬP NHẬT THÔNG TIN LOẠI DỊCH VỤ
    @PutMapping("/{maLoai}")
    public ResponseEntity<?> updateServiceType(
            @PathVariable Integer maLoai, 
            @Valid @RequestBody LoaiDichVuUpdateDTO updateDTO) {
        try {
            // Gọi Service để cập nhật
            LoaiDichVu updatedType = loaiDichVuService.updateServiceType(maLoai, updateDTO);
            
            return ResponseEntity.ok(updatedType);
            
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404
            
        } catch (ResourceAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409
            
        } catch (IllegalArgumentException e) { // <-- Xử lý lỗi trạng thái không hợp lệ
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request
            
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi hệ thống khi cập nhật loại dịch vụ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    //API XÓA MỀM LOẠI DỊCH VỤ
    /**
     * Endpoint DELETE để thực hiện xóa mềm (Soft Delete) loại dịch vụ.
     * @param maLoai Mã loại dịch vụ cần xóa.
     * @return ResponseEntity chứa thông báo thành công hoặc lỗi.
     */
    @DeleteMapping("/{maLoai}")
    public ResponseEntity<?> softDeleteServiceType(@PathVariable Integer maLoai) {
        try {
            // Gọi Service để xóa mềm
            LoaiDichVu deletedType = loaiDichVuService.softDeleteServiceType(maLoai);
            
            // Trả về đối tượng vừa xóa mềm với HTTP Status 200 OK
            return ResponseEntity.ok(deletedType);
            
        } catch (ResourceNotFoundException e) {
            // Lỗi không tìm thấy (404 Not Found)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            
        } catch (IllegalStateException e) {
            // Lỗi nghiệp vụ đã bị xóa (409 Conflict)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            
        } catch (Exception e) {
            // Lỗi khác (500 Internal Server Error)
            return new ResponseEntity<>("Lỗi hệ thống khi xóa mềm loại dịch vụ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}