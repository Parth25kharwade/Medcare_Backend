package com.medcare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "symptoms")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Symptom {

    public enum Severity { MILD, MODERATE, SEVERE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "symptom_name", nullable = false, length = 100)
    private String symptomName;

    @Enumerated(EnumType.STRING)
    private Severity severity = Severity.MILD;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(columnDefinition = "TEXT")
    private String notes;

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
