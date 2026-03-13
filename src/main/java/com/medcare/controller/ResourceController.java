package com.medcare.controller;

import com.medcare.dto.ApiResponse;
import com.medcare.dto.ResourceDto;
import com.medcare.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@PreAuthorize("hasRole('ADMIN')")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PutMapping("/icu")
    public ResponseEntity<ApiResponse<ResourceDto.IcuResponse>> updateIcu(
            @Valid @RequestBody ResourceDto.IcuUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("ICU updated", resourceService.updateIcuBeds(request)));
    }

    @GetMapping("/icu/{hospitalId}")
    public ResponseEntity<ApiResponse<ResourceDto.IcuResponse>> getIcuStatus(
            @PathVariable Long hospitalId) {
        return ResponseEntity.ok(ApiResponse.success(resourceService.getIcuStatus(hospitalId)));
    }

    @PutMapping("/staff")
    public ResponseEntity<ApiResponse<ResourceDto.StaffResponse>> updateStaff(
            @Valid @RequestBody ResourceDto.StaffUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Staff updated", resourceService.updateStaff(request)));
    }

    @GetMapping("/staff/{hospitalId}")
    public ResponseEntity<ApiResponse<List<ResourceDto.StaffResponse>>> getStaff(
            @PathVariable Long hospitalId) {
        return ResponseEntity.ok(ApiResponse.success(resourceService.getStaffStatus(hospitalId)));
    }

    @PostMapping("/predict/{hospitalId}")
    public ResponseEntity<ApiResponse<ResourceDto.ForecastResponse>> predictStress(
            @PathVariable Long hospitalId) {
        return ResponseEntity.ok(ApiResponse.success("Prediction complete",
                resourceService.predictResourceStress(hospitalId)));
    }

    @GetMapping("/forecast/{hospitalId}")
    public ResponseEntity<ApiResponse<List<ResourceDto.ForecastResponse>>> getForecastHistory(
            @PathVariable Long hospitalId) {
        return ResponseEntity.ok(ApiResponse.success(resourceService.getForecastHistory(hospitalId)));
    }
}
