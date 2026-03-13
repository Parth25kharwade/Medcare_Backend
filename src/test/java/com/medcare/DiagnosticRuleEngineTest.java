package com.medcare;

import com.medcare.ai.DiagnosticRuleEngine;
import com.medcare.dto.DiagnosticDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiagnosticRuleEngineTest {

    private DiagnosticRuleEngine engine;

    @BeforeEach
    void setUp() {
        engine = new DiagnosticRuleEngine();
    }

    @Test
    void testDengueDetection() {
        DiagnosticDto.VitalsInput vitals = new DiagnosticDto.VitalsInput();
        vitals.setTemperature(new BigDecimal("39.5"));   // High fever

        DiagnosticDto.LabInput lab = new DiagnosticDto.LabInput();
        lab.setPlateletCount(80000);                      // Low platelet

        DiagnosticDto.AnalysisResult result = engine.analyze(
                1L, "Test Patient",
                List.of("fever", "headache", "rash"),
                vitals, lab
        );

        assertNotNull(result);
        assertTrue(result.getRiskScore() > 20.0);
        assertTrue(result.getSuspectedConditions().stream()
                .anyMatch(c -> c.contains("Dengue")));
        assertEquals("HIGH", result.getRiskLevel());
    }

    @Test
    void testCardiacRiskDetection() {
        DiagnosticDto.VitalsInput vitals = new DiagnosticDto.VitalsInput();
        vitals.setSystolicBp(160);   // High BP

        DiagnosticDto.AnalysisResult result = engine.analyze(
                2L, "Cardiac Patient",
                List.of("chest pain", "palpitation"),
                vitals, null
        );

        assertNotNull(result);
        assertTrue(result.getSuspectedConditions().stream()
                .anyMatch(c -> c.contains("Cardiac") || c.contains("Coronary")));
        assertTrue(result.getRiskScore() >= 40.0);
    }

    @Test
    void testLowOxygenDetection() {
        DiagnosticDto.VitalsInput vitals = new DiagnosticDto.VitalsInput();
        vitals.setOxygenSaturation(new BigDecimal("91.0"));

        DiagnosticDto.AnalysisResult result = engine.analyze(
                3L, "Respiratory Patient",
                List.of("shortness of breath", "cough"),
                vitals, null
        );

        assertNotNull(result);
        assertTrue(result.getSuspectedConditions().stream()
                .anyMatch(c -> c.contains("Respiratory") || c.contains("Hypoxemia")));
        assertEquals("CRITICAL", result.getRiskLevel());
    }

    @Test
    void testNoConditionsHealthyPatient() {
        DiagnosticDto.VitalsInput vitals = new DiagnosticDto.VitalsInput();
        vitals.setTemperature(new BigDecimal("37.0"));
        vitals.setSystolicBp(120);
        vitals.setOxygenSaturation(new BigDecimal("98.0"));

        DiagnosticDto.AnalysisResult result = engine.analyze(
                4L, "Healthy Patient",
                List.of("mild fatigue"),
                vitals, null
        );

        assertNotNull(result);
        assertEquals("LOW", result.getRiskLevel());
        assertEquals(0.0, result.getRiskScore());
    }

    @Test
    void testRiskScoreCappedAt100() {
        DiagnosticDto.VitalsInput vitals = new DiagnosticDto.VitalsInput();
        vitals.setTemperature(new BigDecimal("40.0"));
        vitals.setSystolicBp(170);
        vitals.setOxygenSaturation(new BigDecimal("88.0"));

        DiagnosticDto.LabInput lab = new DiagnosticDto.LabInput();
        lab.setPlateletCount(50000);
        lab.setBloodSugar(new BigDecimal("250.0"));
        lab.setBilirubin(new BigDecimal("3.0"));
        lab.setAlt(new BigDecimal("100.0"));
        lab.setCreatinine(new BigDecimal("2.0"));

        DiagnosticDto.AnalysisResult result = engine.analyze(
                5L, "Critical Patient",
                List.of("fever", "chest pain", "shortness of breath", "thirst"),
                vitals, lab
        );

        assertNotNull(result);
        assertTrue(result.getRiskScore() <= 100.0);
        assertEquals("CRITICAL", result.getRiskLevel());
    }
}
