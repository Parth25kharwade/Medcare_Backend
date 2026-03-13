package com.medcare.repository;

import com.medcare.entity.Vitals;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VitalsRepository extends JpaRepository<Vitals, Long> {
    List<Vitals> findByPatientIdOrderByRecordedAtDesc(Long patientId);
    Optional<Vitals> findTopByPatientIdOrderByRecordedAtDesc(Long patientId);
}
