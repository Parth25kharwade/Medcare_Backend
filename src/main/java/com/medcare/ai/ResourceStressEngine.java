package com.medcare.ai;

import com.medcare.dto.ResourceDto;
import com.medcare.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Resource Stress Prediction Engine
 * Analyzes ICU, staff, and equipment data to predict hospital resource stress.
 */
@Component
public class ResourceStressEngine {

    public ResourceDto.ForecastResponse predict(Hospital hospital,
                                                IcuBed icuBed,
                                                List<Staff> staffList,
                                                List<Equipment> equipmentList) {

        List<String> alerts = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        // ── ICU Stress Score ──────────────────────────────────────────
        double icuStress = 0.0;
        if (icuBed != null && icuBed.getTotalBeds() > 0) {
            double utilization = (double) icuBed.getOccupiedBeds() / icuBed.getTotalBeds() * 100;
            icuStress = utilization;

            if (utilization >= 90) {
                alerts.add("🚨 ICU CRITICAL: " + String.format("%.1f", utilization) + "% occupancy — immediate capacity expansion needed");
                recommendations.add("Activate overflow protocol; transfer non-critical ICU patients to step-down units");
            } else if (utilization >= 80) {
                alerts.add("⚠️ ICU HIGH: " + String.format("%.1f", utilization) + "% occupancy");
                recommendations.add("Prepare additional ICU beds; review pending discharges");
            } else if (utilization >= 60) {
                alerts.add("ℹ️ ICU ELEVATED: " + String.format("%.1f", utilization) + "% occupancy — monitor closely");
            }
        }

        // ── Staff Stress Score ────────────────────────────────────────
        double maxStaffWorkload = 0.0;
        for (Staff staff : staffList) {
            double workload = staff.getWorkloadPct() != null
                    ? staff.getWorkloadPct().doubleValue() : 0.0;
            maxStaffWorkload = Math.max(maxStaffWorkload, workload);

            if (workload >= 90) {
                alerts.add("🚨 STAFF CRITICAL: " + staff.getDepartment()
                        + " at " + String.format("%.1f", workload) + "% workload");
                recommendations.add("Emergency staff rotation for " + staff.getDepartment()
                        + "; consider calling in off-duty staff");
            } else if (workload >= 80) {
                alerts.add("⚠️ STAFF HIGH: " + staff.getDepartment()
                        + " at " + String.format("%.1f", workload) + "% workload");
                recommendations.add("Schedule additional staff for " + staff.getDepartment());
            }
        }

        // ── Equipment Stress Score ────────────────────────────────────
        double maxEquipmentStress = 0.0;
        for (Equipment eq : equipmentList) {
            if (eq.getTotalUnits() > 0) {
                double usage = (double) eq.getInUseUnits() / eq.getTotalUnits() * 100;
                maxEquipmentStress = Math.max(maxEquipmentStress, usage);

                if (usage >= 90) {
                    alerts.add("🚨 EQUIPMENT CRITICAL: " + eq.getEquipmentName()
                            + " at " + String.format("%.1f", usage) + "% usage");
                    recommendations.add("Emergency procurement of " + eq.getEquipmentName()
                            + "; activate inter-hospital sharing protocol");
                } else if (usage >= 75) {
                    alerts.add("⚠️ EQUIPMENT HIGH: " + eq.getEquipmentName()
                            + " demand elevated");
                    recommendations.add("Expedite maintenance of idle " + eq.getEquipmentName() + " units");
                }
            }
        }

        // ── Overall Score (weighted average) ─────────────────────────
        double overallScore = (icuStress * 0.4) + (maxStaffWorkload * 0.35) + (maxEquipmentStress * 0.25);

        ResourceForecast.StressLevel stressLevel;
        if (overallScore >= 80)      stressLevel = ResourceForecast.StressLevel.CRITICAL;
        else if (overallScore >= 60) stressLevel = ResourceForecast.StressLevel.HIGH;
        else if (overallScore >= 40) stressLevel = ResourceForecast.StressLevel.ELEVATED;
        else                         stressLevel = ResourceForecast.StressLevel.NORMAL;

        if (recommendations.isEmpty()) {
            recommendations.add("Resource levels are within acceptable range. Continue routine monitoring.");
        }

        ResourceDto.ForecastResponse response = new ResourceDto.ForecastResponse();
        response.setHospitalId(hospital.getId());
        response.setHospitalName(hospital.getName());
        response.setStressLevel(stressLevel.name());
        response.setIcuStressScore(bd(icuStress));
        response.setStaffStressScore(bd(maxStaffWorkload));
        response.setEquipmentStressScore(bd(maxEquipmentStress));
        response.setOverallStressScore(bd(overallScore));
        response.setAlerts(alerts);
        response.setRecommendations(recommendations);
        response.setForecastDate(LocalDateTime.now().toString());

        return response;
    }

    private BigDecimal bd(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
