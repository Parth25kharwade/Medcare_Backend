package com.medcare.service;

import com.medcare.ai.DiagnosticRuleEngine;
import com.medcare.dto.DiagnosticDto;
import com.medcare.entity.*;
import com.medcare.repository.*;
import com.medcare.security.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DiagnosticService {

    private final DiagnosticRuleEngine ruleEngine;
    private final PatientService patientService;
    private final DiagnosticAnalysisRepository analysisRepository;
    private final UserRepository userRepository;

    public DiagnosticService(DiagnosticRuleEngine ruleEngine,
                              PatientService patientService,
                              DiagnosticAnalysisRepository analysisRepository,
                              UserRepository userRepository) {
        this.ruleEngine = ruleEngine;
        this.patientService = patientService;
        this.analysisRepository = analysisRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public DiagnosticDto.AnalysisResult analyzePatient(DiagnosticDto.AnalyzeRequest request) {
        Patient patient = patientService.getPatientEntity(request.getPatientId());

        DiagnosticDto.AnalysisResult result = ruleEngine.analyze(
                patient.getId(),
                patient.getFullName(),
                request.getSymptoms(),
                request.getVitals(),
                request.getLabReport()
        );

        // Persist analysis to database
        DiagnosticAnalysis analysis = DiagnosticAnalysis.builder()
                .patient(patient)
                .riskScore(BigDecimal.valueOf(result.getRiskScore()))
                .riskLevel(DiagnosticAnalysis.RiskLevel.valueOf(result.getRiskLevel()))
                .suspectedConditions(String.join(" | ", result.getSuspectedConditions()))
                .diagnosticAlerts(String.join(" | ", result.getDiagnosticAlerts()))
                .recommendations(String.join(" | ", result.getRecommendations()))
                .build();

        // Attach current user as analyzer
        try {
            UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            userRepository.findById(principal.getId()).ifPresent(analysis::setAnalyzedBy);
        } catch (Exception ignored) {}

        analysisRepository.save(analysis);
        return result;
    }

    @Transactional(readOnly = true)
    public List<DiagnosticDto.AnalysisResponse> getPatientHistory(Long patientId) {
        return analysisRepository.findByPatientIdOrderByAnalyzedAtDesc(patientId)
                .stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<DiagnosticDto.AnalysisResponse> getCriticalAlerts(Long doctorId) {
        return analysisRepository.findByPatientDoctorIdAndRiskLevelIn(
                        doctorId,
                        List.of(DiagnosticAnalysis.RiskLevel.HIGH, DiagnosticAnalysis.RiskLevel.CRITICAL))
                .stream().map(this::mapToResponse).toList();
    }

    private DiagnosticDto.AnalysisResponse mapToResponse(DiagnosticAnalysis a) {
        DiagnosticDto.AnalysisResponse r = new DiagnosticDto.AnalysisResponse();
        r.setId(a.getId());
        r.setPatientId(a.getPatient().getId());
        r.setPatientName(a.getPatient().getFullName());
        r.setRiskScore(a.getRiskScore());
        r.setRiskLevel(a.getRiskLevel().name());
        r.setSuspectedConditions(a.getSuspectedConditions());
        r.setDiagnosticAlerts(a.getDiagnosticAlerts());
        r.setRecommendations(a.getRecommendations());
        r.setAnalyzedAt(a.getAnalyzedAt() != null ? a.getAnalyzedAt().toString() : null);
        return r;
    }
}
