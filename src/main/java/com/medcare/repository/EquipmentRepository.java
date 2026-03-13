package com.medcare.repository;

import com.medcare.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByHospitalId(Long hospitalId);
}
