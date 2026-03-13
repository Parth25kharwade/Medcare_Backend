package com.medcare.repository;

import com.medcare.entity.LabReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LabReportRepository extends JpaRepository<LabReport, Long> {
    List<LabReport> findByPatientIdOrderByReportDateDesc(Long patientId);
    Optional<LabReport> findTopByPatientIdOrderByReportDateDesc(Long patientId);
}
