package com.medcare.controller;

import com.medcare.dto.ApiResponse;
import com.medcare.dto.MedicalRecordDto;
import com.medcare.dto.PatientDto;
import com.medcare.service.MedicalRecordService;
import com.medcare.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final MedicalRecordService medicalRecordService;

    public PatientController(PatientService patientService,
                             MedicalRecordService medicalRecordService) {
        this.patientService = patientService;
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PatientDto.Response>> createPatient(
            @Valid @RequestBody PatientDto.CreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Patient created", patientService.createPatient(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientDto.Response>> updatePatient(
            @PathVariable Long id,
            @RequestBody PatientDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Patient updated", patientService.updatePatient(id, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientDto.Response>> getPatient(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(patientService.getPatient(id)));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<List<PatientDto.Response>>> getPatientsByDoctor(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(ApiResponse.success(patientService.getPatientsByDoctor(doctorId)));
    }

    @GetMapping("/hospital/{hospitalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PatientDto.Response>>> getPatientsByHospital(
            @PathVariable Long hospitalId) {
        return ResponseEntity.ok(ApiResponse.success(patientService.getPatientsByHospital(hospitalId)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivatePatient(@PathVariable Long id) {
        patientService.deactivatePatient(id);
        return ResponseEntity.ok(ApiResponse.success("Patient deactivated", null));
    }

    // ── Medical Records ────────────────────────────────────────────────

    @PostMapping("/records")
    public ResponseEntity<ApiResponse<MedicalRecordDto.Response>> createRecord(
            @Valid @RequestBody MedicalRecordDto.CreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Record created",
                medicalRecordService.createRecord(request)));
    }

    @PostMapping("/records/{recordId}/upload")
    public ResponseEntity<ApiResponse<MedicalRecordDto.Response>> uploadFile(
            @PathVariable Long recordId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(ApiResponse.success("File uploaded",
                medicalRecordService.uploadFile(recordId, file)));
    }

    @GetMapping("/{patientId}/history")
    public ResponseEntity<ApiResponse<List<MedicalRecordDto.Response>>> getPatientHistory(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.success(medicalRecordService.getPatientHistory(patientId)));
    }
}
