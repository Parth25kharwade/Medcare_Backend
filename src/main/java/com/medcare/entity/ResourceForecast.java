package com.medcare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource_forecasts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResourceForecast {

    public enum StressLevel { NORMAL, ELEVATED, HIGH, CRITICAL }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Enumerated(EnumType.STRING)
    @Column(name = "stress_level", nullable = false)
    private StressLevel stressLevel;

    @Column(name = "icu_stress_score", precision = 5, scale = 2)
    private BigDecimal icuStressScore;

    @Column(name = "staff_stress_score", precision = 5, scale = 2)
    private BigDecimal staffStressScore;

    @Column(name = "equipment_stress_score", precision = 5, scale = 2)
    private BigDecimal equipmentStressScore;

    @Column(name = "overall_stress_score", precision = 5, scale = 2)
    private BigDecimal overallStressScore;

    @Column(columnDefinition = "TEXT")
    private String alerts;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "forecast_date")
    private LocalDateTime forecastDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by")
    private User generatedBy;

    @PrePersist
    protected void onCreate() {
        forecastDate = LocalDateTime.now();
    }
}
