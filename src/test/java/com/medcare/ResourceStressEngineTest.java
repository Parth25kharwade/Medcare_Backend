package com.medcare;

import com.medcare.ai.ResourceStressEngine;
import com.medcare.dto.ResourceDto;
import com.medcare.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResourceStressEngineTest {

    private ResourceStressEngine engine;
    private Hospital hospital;

    @BeforeEach
    void setUp() {
        engine = new ResourceStressEngine();
        hospital = Hospital.builder()
                .id(1L).name("Test Hospital").totalBeds(500).build();
    }

    @Test
    void testCriticalIcuStress() {
        IcuBed icuBed = IcuBed.builder()
                .hospital(hospital)
                .totalBeds(50)
                .occupiedBeds(48)   // 96% → CRITICAL
                .build();

        ResourceDto.ForecastResponse result = engine.predict(hospital, icuBed, List.of(), List.of());

        assertNotNull(result);
        assertTrue(result.getIcuStressScore().doubleValue() > 90);
        assertTrue(result.getAlerts().stream().anyMatch(a -> a.contains("ICU CRITICAL")));
    }

    @Test
    void testCriticalStaffWorkload() {
        Staff staff = Staff.builder()
                .hospital(hospital)
                .department("ICU")
                .totalStaff(10)
                .onDutyStaff(10)
                .workloadPct(new BigDecimal("100.0"))
                .build();

        ResourceDto.ForecastResponse result = engine.predict(hospital, null, List.of(staff), List.of());

        assertNotNull(result);
        assertTrue(result.getStaffStressScore().doubleValue() >= 90);
        assertTrue(result.getAlerts().stream().anyMatch(a -> a.contains("STAFF CRITICAL")));
    }

    @Test
    void testNormalResourceLevel() {
        IcuBed icuBed = IcuBed.builder()
                .hospital(hospital)
                .totalBeds(50)
                .occupiedBeds(15)   // 30% — NORMAL
                .build();

        Staff staff = Staff.builder()
                .hospital(hospital)
                .department("General")
                .totalStaff(20)
                .onDutyStaff(10)
                .workloadPct(new BigDecimal("50.0"))
                .build();

        ResourceDto.ForecastResponse result = engine.predict(hospital, icuBed, List.of(staff), List.of());

        assertNotNull(result);
        assertEquals("NORMAL", result.getStressLevel());
    }

    @Test
    void testOverallStressScoreWeighting() {
        IcuBed icuBed = IcuBed.builder()
                .hospital(hospital).totalBeds(10).occupiedBeds(9).build(); // 90%

        Staff staff = Staff.builder()
                .hospital(hospital).department("ICU").totalStaff(10).onDutyStaff(9)
                .workloadPct(new BigDecimal("90.0")).build();

        ResourceDto.ForecastResponse result = engine.predict(hospital, icuBed, List.of(staff), List.of());

        // Overall = (90*0.4) + (90*0.35) + (0*0.25) = 36 + 31.5 = 67.5 → HIGH
        assertNotNull(result);
        assertTrue(result.getOverallStressScore().doubleValue() > 60);
        assertEquals("HIGH", result.getStressLevel());
    }
}
