package com.medcare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "diagnostic_analyses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DiagnosticAnalysis {

    public enum RiskLevel { LOW, MEDIUM, HIGH, CRITICAL }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "risk_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal riskScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    private RiskLevel riskLevel;

    @Column(name = "suspected_conditions", columnDefinition = "TEXT")
    private String suspectedConditions;

    @Column(name = "diagnostic_alerts", columnDefinition = "TEXT")
    private String diagnosticAlerts;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analyzed_by")
    private User analyzedBy;

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    @PrePersist
    protected void onCreate() {
        analyzedAt = LocalDateTime.now();
    }
}
