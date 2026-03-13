package com.medcare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vitals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vitals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(precision = 5, scale = 2)
    private BigDecimal temperature;

    @Column(name = "systolic_bp")
    private Integer systolicBp;

    @Column(name = "diastolic_bp")
    private Integer diastolicBp;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "oxygen_saturation", precision = 5, scale = 2)
    private BigDecimal oxygenSaturation;

    @Column(name = "respiratory_rate")
    private Integer respiratoryRate;

    @Column(name = "weight_kg", precision = 6, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "height_cm", precision = 6, scale = 2)
    private BigDecimal heightCm;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by")
    private User recordedBy;

    @PrePersist
    protected void onCreate() {
        recordedAt = LocalDateTime.now();
    }
}
