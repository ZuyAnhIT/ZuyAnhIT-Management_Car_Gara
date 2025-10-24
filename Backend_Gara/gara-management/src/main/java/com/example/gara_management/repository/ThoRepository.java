package com.example.gara_management.repository;

import com.example.gara_management.model.Tho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThoRepository extends JpaRepository<Tho, Integer>, 
                                      JpaSpecificationExecutor<Tho> { 

    // /**
    //  * Dùng để kiểm tra tên thợ đã tồn tại chưa (tùy chọn, vì tên có thể trùng).
    //  */
    // Optional<Tho> findByTenTho(String tenTho);
    
    /**
     * Dùng để kiểm tra trùng SĐT.
     */
    Optional<Tho> findBySoDienThoai(String soDienThoai);
    
    /**
     * Dùng để kiểm tra trùng Email.
     */
    Optional<Tho> findByEmail(String email);

    long countByKinhNghiemGreaterThan(int years);
    long countByKinhNghiemLessThanEqual(int years);
}