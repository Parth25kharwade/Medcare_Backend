package com.medcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MedicalRecordDto {

    @Data
    public static class CreateRequest {
        @NotNull
        private Long patientId;

        @NotBlank
        private String recordType;

        @NotBlank
        private String title;

        private String description;
        private String diagnosis;
        private String treatment;
        private String medications;
        private String allergies;

        @NotNull
        private LocalDate visitDate;
    }

    @Data
    public static class Response {
        private Long id;
        private Long patientId;
        private String patientName;
        private String recordType;
        private String title;
        private String description;
        private String diagnosis;
        private String treatment;
        private String medications;
        private String allergies;
        private String filePath;
        private LocalDate visitDate;
        private String doctorName;
        private LocalDateTime createdAt;
    }
}
