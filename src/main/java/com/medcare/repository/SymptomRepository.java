package com.medcare.repository;

import com.medcare.entity.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SymptomRepository extends JpaRepository<Symptom, Long> {
    List<Symptom> findByPatientIdOrderByRecordedAtDesc(Long patientId);
}
