package com.example.gara_management.controller;

import com.example.gara_management.dto.PageResponseDTO;
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
import org.springframework.web.bind.annotation.*;

@Tag(name = "Quản lý Xe", description = "API hiển thị và tìm kiếm xe trong hệ thống gara")
@RestController
@RequestMapping("/api/xe")
public class XeController {

    private final XeService xeService;

    public XeController(XeService xeService) {
        this.xeService = xeService;
    }

    // ==========================================================
    // 🟢 API HIỂN THỊ DANH SÁCH & SẮP XẾP
    // ==========================================================
    /**
     * Endpoint GET để lấy danh sách xe (không có tìm kiếm/lọc).
     * Ví dụ: /api/xe/hienThiDanhSach?sortBy=maXe&sortDirection=asc
     */
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
    // 🟢 API TÌM KIẾM, PHÂN TRANG & SẮP XẾP
    // ==========================================================
    /**
     * Endpoint GET để tìm kiếm xe theo các tiêu chí:
     * - Biển số (contains)
     * - Hãng xe (contains)
     * - Năm sản xuất (equals)
     * - Màu sắc (contains)
     * - Trạng thái (contains)
     *
     * Ví dụ: /api/xe/timKiem?bienSo=51A&hangXe=Toyota&namSanXuat=2020
     */
    @GetMapping("/timKiem")
    public ResponseEntity<PageResponseDTO<XeResponseDTO>> searchXe(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String bienSo,
            @RequestParam(required = false) String hangXe,
            @RequestParam(required = false) Integer namSanXuat,
            @RequestParam(required = false) String mauSac,
            @RequestParam(required = false) String trangThai) {

        PageResponseDTO<XeResponseDTO> responseDTO =
                xeService.searchXe(page, size, sortBy, sortDirection,
                        bienSo, hangXe, namSanXuat, mauSac, trangThai);

        return ResponseEntity.ok(responseDTO);
    }

    // API CẬP NHẬT THÔNG TIN XE
    @PutMapping("/{maXe}")
    public ResponseEntity<?> updateXe(
            @PathVariable Integer maXe,
            @Valid @RequestBody XeUpdateDTO updateDTO) {
        try {
            // Gọi service cập nhật
            Xe updatedXe = xeService.updateXe(maXe, updateDTO);

            // Trả về đối tượng đã cập nhật
            return ResponseEntity.ok(updatedXe);

        } catch (ResourceNotFoundException e) {
            // ❌ Không tìm thấy xe (404)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (ResourceAlreadyExistsException e) {
            // ❌ Biển số đã tồn tại (409)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);

        } catch (IllegalArgumentException e) {
            // ❌ Trạng thái hoặc năm sản xuất không hợp lệ (400)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            // ❌ Lỗi hệ thống khác (500)
            return new ResponseEntity<>("Lỗi hệ thống khi cập nhật xe: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
