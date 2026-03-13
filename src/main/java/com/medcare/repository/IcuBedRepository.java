package com.medcare.repository;

import com.medcare.entity.IcuBed;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface IcuBedRepository extends JpaRepository<IcuBed, Long> {
    Optional<IcuBed> findByHospitalId(Long hospitalId);
    List<IcuBed> findAll();
}
