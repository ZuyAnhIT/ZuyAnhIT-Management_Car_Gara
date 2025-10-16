package com.example.gara_management.repository;

import com.example.gara_management.model.Tho;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
public interface ThoRepository extends JpaRepository<Tho, Integer>, JpaSpecificationExecutor<Tho>{
    Optional<Tho> findByEmail(String email);
    Optional<Tho> findByNumber(String number);
    
}
