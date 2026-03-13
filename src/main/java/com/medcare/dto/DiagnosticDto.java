package com.medcare.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DiagnosticDto {

    @Data
    public static class VitalsInput {
        private BigDecimal temperature;
        private Integer systolicBp;
        private Integer diastolicBp;
        private Integer heartRate;
        private BigDecimal oxygenSaturation;
        private Integer respiratoryRate;
    }

    @Data
    public static class LabInput {
        private BigDecimal hemoglobin;
        private Integer plateletCount;
        private BigDecimal wbcCount;
        private BigDecimal bloodSugar;
        private BigDecimal creatinine;
        private BigDecimal bilirubin;
        private BigDecimal alt;
        private BigDecimal ast;
        private LocalDate reportDate;
    }

    @Data
    public static class AnalyzeRequest {
        @NotNull(message = "Patient ID is required")
        private Long patientId;

        private List<String> symptoms;
        private VitalsInput vitals;
        private LabInput labReport;
    }

    @Data
    public static class AnalysisResult {
        private Long patientId;
        private String patientName;
        private Double riskScore;
        private String riskLevel;
        private List<String> suspectedConditions;
        private List<String> diagnosticAlerts;
        private List<String> recommendations;
        private String analyzedAt;
    }

    @Data
    public static class AnalysisResponse {
        private Long id;
        private Long patientId;
        private String patientName;
        private BigDecimal riskScore;
        private String riskLevel;
        private String suspectedConditions;
        private String diagnosticAlerts;
        private String recommendations;
        private String analyzedAt;
    }
}
