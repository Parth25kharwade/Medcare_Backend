package com.medcare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Equipment {

    public enum DemandLevel { LOW, MEDIUM, HIGH, CRITICAL }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Column(name = "equipment_name", nullable = false, length = 150)
    private String equipmentName;

    @Column(name = "total_units", nullable = false)
    private Integer totalUnits = 0;

    @Column(name = "in_use_units", nullable = false)
    private Integer inUseUnits = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "demand_level")
    private DemandLevel demandLevel = DemandLevel.LOW;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
