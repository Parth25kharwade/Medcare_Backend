package com.medcare.ai;

import com.medcare.dto.DiagnosticDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Rule-Based AI Diagnostic Engine
 * Hackathon prototype implementing clinical heuristic rules.
 */
@Component
public class DiagnosticRuleEngine {

    public DiagnosticDto.AnalysisResult analyze(
            Long patientId,
            String patientName,
            List<String> symptoms,
            DiagnosticDto.VitalsInput vitals,
            DiagnosticDto.LabInput lab) {

        List<String> conditions = new ArrayList<>();
        List<String> alerts = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        double riskScore = 0.0;

        List<String> lowerSymptoms = symptoms != null
                ? symptoms.stream().map(String::toLowerCase).toList()
                : List.of();

        // ─────────────────────────────────────────────────────────────
        // RULE SET 1 — DENGUE
        // High fever + low platelet count
        // ─────────────────────────────────────────────────────────────
        boolean hasFever = vitals != null && vitals.getTemperature() != null
                && vitals.getTemperature().compareTo(new BigDecimal("38.5")) > 0;
        boolean lowPlatelet = lab != null && lab.getPlateletCount() != null
                && lab.getPlateletCount() < 150000;
        boolean dengueSymptoms = lowerSymptoms.stream()
                .anyMatch(s -> s.contains("fever") || s.contains("rash") || s.contains("headache"));

        if (hasFever && lowPlatelet && dengueSymptoms) {
            conditions.add("Dengue Fever (High Confidence)");
            alerts.add("⚠️ High fever with low platelet count — Dengue suspected");
            recommendations.add("Immediate platelet transfusion evaluation; NS1 antigen test; dengue serology");
            riskScore += 35;
        } else if (hasFever && lowPlatelet) {
            conditions.add("Possible Dengue / Thrombocytopenia");
            alerts.add("⚠️ Low platelet count with elevated temperature");
            recommendations.add("Order NS1 antigen test and complete blood count");
            riskScore += 20;
        }

        // ─────────────────────────────────────────────────────────────
        // RULE SET 2 — CARDIAC RISK
        // High BP + chest pain
        // ─────────────────────────────────────────────────────────────
        boolean highBp = vitals != null && vitals.getSystolicBp() != null
                && vitals.getSystolicBp() >= 140;
        boolean chestPain = lowerSymptoms.stream()
                .anyMatch(s -> s.contains("chest") || s.contains("cardiac") || s.contains("palpitation"));
        boolean highHeartRate = vitals != null && vitals.getHeartRate() != null
                && vitals.getHeartRate() > 100;

        if (highBp && chestPain) {
            conditions.add("Acute Coronary Syndrome / Hypertensive Crisis");
            alerts.add("🚨 CRITICAL: High BP + Chest pain — immediate cardiac evaluation needed");
            recommendations.add("Urgent ECG; cardiac troponin test; nitroglycerin evaluation; cardiology consult");
            riskScore += 40;
        } else if (highBp && highHeartRate) {
            conditions.add("Hypertensive Tachycardia");
            alerts.add("⚠️ Elevated BP with high heart rate");
            recommendations.add("BP monitoring; beta-blocker evaluation; rule out secondary causes");
            riskScore += 20;
        }

        // ─────────────────────────────────────────────────────────────
        // RULE SET 3 — RESPIRATORY
        // Low oxygen saturation
        // ─────────────────────────────────────────────────────────────
        boolean lowOxygen = vitals != null && vitals.getOxygenSaturation() != null
                && vitals.getOxygenSaturation().compareTo(new BigDecimal("94")) < 0;
        boolean respiratorySymptoms = lowerSymptoms.stream()
                .anyMatch(s -> s.contains("breath") || s.contains("cough") || s.contains("wheeze"));

        if (lowOxygen && respiratorySymptoms) {
            conditions.add("Respiratory Distress / Possible Pneumonia");
            alerts.add("🚨 CRITICAL: Oxygen saturation below 94% — respiratory support may be needed");
            recommendations.add("Chest X-ray; arterial blood gas; supplemental oxygen; consider bronchodilators");
            riskScore += 40;
        } else if (lowOxygen) {
            conditions.add("Hypoxemia — Cause Unknown");
            alerts.add("⚠️ Low oxygen saturation detected");
            recommendations.add("Pulse oximetry monitoring; ABG test; assess for airway obstruction");
            riskScore += 25;
        }

        // ─────────────────────────────────────────────────────────────
        // RULE SET 4 — DIABETES
        // High blood sugar
        // ─────────────────────────────────────────────────────────────
        boolean highBloodSugar = lab != null && lab.getBloodSugar() != null
                && lab.getBloodSugar().compareTo(new BigDecimal("200")) > 0;
        boolean diabeticSymptoms = lowerSymptoms.stream()
                .anyMatch(s -> s.contains("thirst") || s.contains("urination") || s.contains("fatigue"));

        if (highBloodSugar && diabeticSymptoms) {
            conditions.add("Uncontrolled Diabetes Mellitus / Possible DKA");
            alerts.add("⚠️ High blood glucose with classic symptoms");
            recommendations.add("HbA1c test; urine ketones; insulin evaluation; endocrinology referral");
            riskScore += 25;
        } else if (highBloodSugar) {
            conditions.add("Hyperglycemia");
            alerts.add("⚠️ Elevated blood sugar levels");
            recommendations.add("Fasting glucose test; HbA1c evaluation; dietary assessment");
            riskScore += 15;
        }

        // ─────────────────────────────────────────────────────────────
        // RULE SET 5 — LIVER / JAUNDICE
        // High bilirubin + elevated AST/ALT
        // ─────────────────────────────────────────────────────────────
        boolean highBilirubin = lab != null && lab.getBilirubin() != null
                && lab.getBilirubin().compareTo(new BigDecimal("2.0")) > 0;
        boolean elevatedLiverEnzymes = lab != null
                && ((lab.getAlt() != null && lab.getAlt().compareTo(new BigDecimal("56")) > 0)
                 || (lab.getAst() != null && lab.getAst().compareTo(new BigDecimal("40")) > 0));

        if (highBilirubin && elevatedLiverEnzymes) {
            conditions.add("Hepatic Dysfunction / Jaundice");
            alerts.add("⚠️ Elevated bilirubin and liver enzymes — liver damage suspected");
            recommendations.add("Liver function tests; ultrasound abdomen; hepatitis serology; gastroenterology consult");
            riskScore += 25;
        }

        // ─────────────────────────────────────────────────────────────
        // RULE SET 6 — KIDNEY DISEASE
        // High creatinine
        // ─────────────────────────────────────────────────────────────
        boolean highCreatinine = lab != null && lab.getCreatinine() != null
                && lab.getCreatinine().compareTo(new BigDecimal("1.2")) > 0;

        if (highCreatinine) {
            conditions.add("Renal Impairment / CKD Risk");
            alerts.add("⚠️ Elevated creatinine — kidney function compromised");
            recommendations.add("GFR estimation; urine analysis; nephrology referral; hydration assessment");
            riskScore += 15;
        }

        // ─────────────────────────────────────────────────────────────
        // CAP RISK SCORE AT 100
        // ─────────────────────────────────────────────────────────────
        riskScore = Math.min(riskScore, 100.0);

        if (conditions.isEmpty()) {
            conditions.add("No critical conditions flagged by current rule set");
            recommendations.add("Continue routine monitoring; update lab results for comprehensive analysis");
        }

        // ─────────────────────────────────────────────────────────────
        // RISK LEVEL CLASSIFICATION
        // ─────────────────────────────────────────────────────────────
        String riskLevel;
        if (riskScore >= 70)      riskLevel = "CRITICAL";
        else if (riskScore >= 45) riskLevel = "HIGH";
        else if (riskScore >= 20) riskLevel = "MEDIUM";
        else                      riskLevel = "LOW";

        DiagnosticDto.AnalysisResult result = new DiagnosticDto.AnalysisResult();
        result.setPatientId(patientId);
        result.setPatientName(patientName);
        result.setRiskScore(Math.round(riskScore * 100.0) / 100.0);
        result.setRiskLevel(riskLevel);
        result.setSuspectedConditions(conditions);
        result.setDiagnosticAlerts(alerts);
        result.setRecommendations(recommendations);
        result.setAnalyzedAt(java.time.LocalDateTime.now().toString());

        return result;
    }
}
