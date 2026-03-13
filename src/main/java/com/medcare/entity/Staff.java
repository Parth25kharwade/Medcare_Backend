package com.medcare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(name = "total_staff", nullable = false)
    private Integer totalStaff = 0;

    @Column(name = "on_duty_staff", nullable = false)
    private Integer onDutyStaff = 0;

    @Column(name = "workload_pct", precision = 5, scale = 2)
    private BigDecimal workloadPct = BigDecimal.ZERO;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
