package com.medcare.controller;

import com.medcare.dto.ApiResponse;
import com.medcare.dto.DiagnosticDto;
import com.medcare.security.UserDetailsImpl;
import com.medcare.service.DiagnosticService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnosis")
public class DiagnosticController {

    private final DiagnosticService diagnosticService;

    public DiagnosticController(DiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    /**
     * Core AI diagnostic endpoint
     * POST /api/diagnosis/analyze
     */
    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse<DiagnosticDto.AnalysisResult>> analyze(
            @Valid @RequestBody DiagnosticDto.AnalyzeRequest request) {
        DiagnosticDto.AnalysisResult result = diagnosticService.analyzePatient(request);
        return ResponseEntity.ok(ApiResponse.success("Diagnostic analysis complete", result));
    }

    @GetMapping("/patient/{patientId}/history")
    public ResponseEntity<ApiResponse<List<DiagnosticDto.AnalysisResponse>>> getHistory(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.success(diagnosticService.getPatientHistory(patientId)));
    }

    @GetMapping("/alerts/doctor")
    public ResponseEntity<ApiResponse<List<DiagnosticDto.AnalysisResponse>>> getDoctorAlerts(
            @AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(ApiResponse.success(
                diagnosticService.getCriticalAlerts(principal.getId())));
    }
}
