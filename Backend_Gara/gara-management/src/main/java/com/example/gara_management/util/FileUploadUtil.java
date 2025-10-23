package com.example.gara_management.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID; // Dùng UUID để tạo tên file ngẫu nhiên

public class FileUploadUtil {
    
    private static final String UPLOAD_DIR_NAME = "uploads/images";
    private static final Path UPLOAD_PATH = Paths.get(UPLOAD_DIR_NAME).toAbsolutePath().normalize();
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
    /**
     * Lưu file ảnh vào thư mục cấu hình và tạo tên ngẫu nhiên.
     * @param file MultipartFile từ request
     * @return Tên file duy nhất (UUID + extension) để lưu vào DB.
     * @throws IOException
     */
    public static String saveImageFile(MultipartFile file) throws IOException {
        
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File quá lớn. Kích thước tối đa là 5MB");
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        
        if (originalFilename != null) {
            int lastDot = originalFilename.lastIndexOf('.');
            if (lastDot > 0) {
                extension = originalFilename.substring(lastDot);
            }
            // Thêm validation cơ bản về định dạng ảnh ở đây (tùy chọn)
        }
        
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        if (Files.notExists(UPLOAD_PATH)) {
            Files.createDirectories(UPLOAD_PATH);
        }
        
        Path savePath = UPLOAD_PATH.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), savePath);
        
        return uniqueFilename;
    }
    
    /**
     * Xóa file ảnh dựa trên tên file lưu trong DB.
     */
    public static boolean deleteImageFile(String uniqueFilename) {
        if (uniqueFilename == null || uniqueFilename.isEmpty()) {
            return false;
        }
        
        try {
            Path filePath = UPLOAD_PATH.resolve(uniqueFilename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }
    
    public static Path getUploadPath() {
        return UPLOAD_PATH;
    }
}