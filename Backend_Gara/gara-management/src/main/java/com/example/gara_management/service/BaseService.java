package com.example.gara_management.service;

import com.example.gara_management.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// T là kiểu Entity, ID là kiểu khóa chính
public abstract class BaseService<T, ID> {

    // Đây là logic tái sử dụng chính
    @Transactional
    public T softDelete(ID id, JpaRepository<T, ID> repository, String entityName) {
        
        Optional<T> entityOptional = repository.findById(id);
        
        // 1. Kiểm tra tồn tại
        if (entityOptional.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy " + entityName + " với Mã: " + id);
        }
        
        T entity = entityOptional.get();
        
        // Cần đảm bảo Entity có getter/setter cho trường trangThai
        String currentTrangThai;
        try {
            // Dùng reflection hoặc Interface (tốt hơn) để lấy trạng thái
            currentTrangThai = (String) entity.getClass().getMethod("getTrangThai").invoke(entity);
            
            // 2. Kiểm tra nếu đã ở trạng thái "Đã xóa"
            if ("Đã xóa".equals(currentTrangThai)) {
                throw new IllegalStateException(entityName + " này đã ở trạng thái 'Đã xóa' và không thể xóa tiếp.");
            }
            
            // 3. Thực hiện xóa mềm
            entity.getClass().getMethod("setTrangThai", String.class).invoke(entity, "Đã xóa");
            
        } catch (Exception e) {
            // Xử lý lỗi nếu Entity không có phương thức getTrangThai/setTrangThai
            throw new RuntimeException("Lỗi: " + entityName + " không hỗ trợ Soft Delete (thiếu trường 'trangThai').");
        }
        
        // 4. Lưu và trả về
        return repository.save(entity);
    }
}