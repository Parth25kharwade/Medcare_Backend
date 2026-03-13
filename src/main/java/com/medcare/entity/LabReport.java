package com.medcare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_reports")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LabReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "report_type", nullable = false, length = 100)
    private String reportType;

    @Column(precision = 5, scale = 2)
    private BigDecimal hemoglobin;

    @Column(name = "platelet_count")
    private Integer plateletCount;

    @Column(name = "wbc_count", precision = 8, scale = 2)
    private BigDecimal wbcCount;

    @Column(name = "rbc_count", precision = 5, scale = 2)
    private BigDecimal rbcCount;

    @Column(name = "blood_sugar", precision = 6, scale = 2)
    private BigDecimal bloodSugar;

    @Column(precision = 5, scale = 2)
    private BigDecimal creatinine;

    @Column(precision = 5, scale = 2)
    private BigDecimal bilirubin;

    @Column(precision = 6, scale = 2)
    private BigDecimal alt;

    @Column(precision = 6, scale = 2)
    private BigDecimal ast;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "lab_name", length = 150)
    private String labName;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
