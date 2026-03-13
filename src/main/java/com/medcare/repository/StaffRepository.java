package com.medcare.repository;

import com.medcare.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByHospitalId(Long hospitalId);
}
