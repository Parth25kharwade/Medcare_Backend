package com.medcare.repository;

import com.medcare.entity.DiagnosticAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiagnosticAnalysisRepository extends JpaRepository<DiagnosticAnalysis, Long> {
    List<DiagnosticAnalysis> findByPatientIdOrderByAnalyzedAtDesc(Long patientId);
    List<DiagnosticAnalysis> findByPatientDoctorIdAndRiskLevelIn(
        Long doctorId, List<DiagnosticAnalysis.RiskLevel> levels);
    List<DiagnosticAnalysis> findByPatientHospitalIdAndRiskLevelIn(
        Long hospitalId, List<DiagnosticAnalysis.RiskLevel> levels);
}
