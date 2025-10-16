package com.example.gara_management.repository;

import com.example.gara_management.model.Mechanic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface MechanicRepository extends JpaRepository<Mechanic, Integer>, JpaSpecificationExecutor<Mechanic> {
    Optional<Mechanic> findByEmail(String email);
    Optional<Mechanic> findBySoDienThoai(String number);
}
