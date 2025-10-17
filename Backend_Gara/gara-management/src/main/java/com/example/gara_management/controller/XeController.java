package com.example.gara_management.controller;

import com.example.gara_management.dto.PageResponseDTO;
import com.example.gara_management.dto.XeDTO.XeResponseDTO;
import com.example.gara_management.exception.ResourceNotFoundException;
import com.example.gara_management.model.Xe;
import com.example.gara_management.service.XeService;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    // ==========================================================
// 🟠 API XÓA MỀM XE
// ==========================================================
    /**
     * Endpoint DELETE để thực hiện xóa mềm (Soft Delete) xe.
     * @param maXe Mã xe cần xóa.
     * @return ResponseEntity chứa đối tượng đã xóa mềm hoặc thông báo lỗi.
     */
    @DeleteMapping("/{maXe}")
    public ResponseEntity<?> softDeleteXe(@PathVariable Integer maXe) {
        try {
            // Gọi Service để xóa mềm
            Xe deletedXe = xeService.softDeleteXe(maXe);

            // Trả về đối tượng vừa xóa mềm với HTTP Status 200 OK
            return ResponseEntity.ok(deletedXe);

        } catch (ResourceNotFoundException e) {
            // Lỗi không tìm thấy (404 Not Found)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (IllegalStateException e) {
            // Lỗi nghiệp vụ: Xe đã bị xóa trước đó (409 Conflict)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);

        } catch (Exception e) {
            // Lỗi khác (500 Internal Server Error)
            return new ResponseEntity<>("Lỗi hệ thống khi xóa mềm xe: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
