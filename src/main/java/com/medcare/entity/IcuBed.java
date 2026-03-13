package com.medcare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "icu_beds")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IcuBed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Column(name = "total_beds", nullable = false)
    private Integer totalBeds = 0;

    @Column(name = "occupied_beds", nullable = false)
    private Integer occupiedBeds = 0;

    // Computed by MySQL as STORED column — not mapped directly; use getter
    @Column(name = "available_beds", insertable = false, updatable = false)
    private Integer availableBeds;

    @Column(name = "utilization_pct", insertable = false, updatable = false, precision = 5, scale = 2)
    private BigDecimal utilizationPct;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
