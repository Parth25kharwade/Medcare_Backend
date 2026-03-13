package com.medcare.controller;

import com.medcare.dto.ApiResponse;
import com.medcare.dto.DiagnosticDto;
import com.medcare.dto.ResourceDto;
import com.medcare.entity.DiagnosticAnalysis;
import com.medcare.repository.*;
import com.medcare.security.UserDetailsImpl;
import com.medcare.service.DiagnosticService;
import com.medcare.service.ResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DiagnosticService diagnosticService;
    private final ResourceService resourceService;
    private final PatientRepository patientRepository;
    private final DiagnosticAnalysisRepository analysisRepository;

    public DashboardController(DiagnosticService diagnosticService,
                               ResourceService resourceService,
                               PatientRepository patientRepository,
                               DiagnosticAnalysisRepository analysisRepository) {
        this.diagnosticService = diagnosticService;
        this.resourceService = resourceService;
        this.patientRepository = patientRepository;
        this.analysisRepository = analysisRepository;
    }

    /**
     * Doctor Dashboard — patient alerts + diagnostic suggestions
     */
    @GetMapping("/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDoctorDashboard(
            @AuthenticationPrincipal UserDetailsImpl principal) {

        Map<String, Object> dashboard = new HashMap<>();

        // Patient count
        long patientCount = patientRepository.findByDoctorIdAndIsActiveTrue(principal.getId()).size();
        dashboard.put("totalActivePatients", patientCount);

        // High & critical alerts
        List<DiagnosticDto.AnalysisResponse> criticalAlerts =
                diagnosticService.getCriticalAlerts(principal.getId());
        dashboard.put("criticalAlerts", criticalAlerts);
        dashboard.put("totalCriticalAlerts", criticalAlerts.size());

        // Summary counts
        long highRisk = criticalAlerts.stream()
                .filter(a -> "HIGH".equals(a.getRiskLevel())).count();
        long critical = criticalAlerts.stream()
                .filter(a -> "CRITICAL".equals(a.getRiskLevel())).count();
        dashboard.put("highRiskCount", highRisk);
        dashboard.put("criticalCount", critical);

        return ResponseEntity.ok(ApiResponse.success("Doctor dashboard loaded", dashboard));
    }

    /**
     * Admin Dashboard — ICU availability, resource stress, equipment demand
     */
    @GetMapping("/admin/{hospitalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminDashboard(
            @PathVariable Long hospitalId,
            @AuthenticationPrincipal UserDetailsImpl principal) {

        Map<String, Object> dashboard = new HashMap<>();

        // ICU Status
        try {
            ResourceDto.IcuResponse icuStatus = resourceService.getIcuStatus(hospitalId);
            dashboard.put("icuStatus", icuStatus);
        } catch (Exception e) {
            dashboard.put("icuStatus", "No ICU data available");
        }

        // Staff Status
        List<ResourceDto.StaffResponse> staffStatus = resourceService.getStaffStatus(hospitalId);
        dashboard.put("staffStatus", staffStatus);

        // Latest Forecast
        List<ResourceDto.ForecastResponse> forecasts = resourceService.getForecastHistory(hospitalId);
        dashboard.put("latestForecast", forecasts.isEmpty() ? null : forecasts.get(0));
        dashboard.put("forecastHistory", forecasts);

        // Resource stress alerts from forecasts
        long criticalForecasts = forecasts.stream()
                .filter(f -> "CRITICAL".equals(f.getStressLevel()) || "HIGH".equals(f.getStressLevel()))
                .count();
        dashboard.put("activeStressAlerts", criticalForecasts);

        // Patient count
        dashboard.put("totalActivePatients",
                patientRepository.countActivePatientsByHospital(hospitalId));

        return ResponseEntity.ok(ApiResponse.success("Admin dashboard loaded", dashboard));
    }
}
