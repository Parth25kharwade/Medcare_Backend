package com.medcare.dto;

import com.medcare.entity.Patient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientDto {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotNull(message = "Date of birth is required")
        private LocalDate dateOfBirth;

        @NotNull(message = "Gender is required")
        private Patient.Gender gender;

        private String bloodGroup;
        private String phone;
        private String address;
        private String emergencyContact;
        private String emergencyPhone;
        private Long doctorId;
        private Long hospitalId;
    }

    @Data
    public static class UpdateRequest {
        private String fullName;
        private LocalDate dateOfBirth;
        private Patient.Gender gender;
        private String bloodGroup;
        private String phone;
        private String address;
        private String emergencyContact;
        private String emergencyPhone;
        private Long doctorId;
    }

    @Data
    public static class Response {
        private Long id;
        private String patientCode;
        private String fullName;
        private LocalDate dateOfBirth;
        private Patient.Gender gender;
        private String bloodGroup;
        private String phone;
        private String address;
        private String emergencyContact;
        private String emergencyPhone;
        private String doctorName;
        private Long doctorId;
        private String hospitalName;
        private Long hospitalId;
        private Boolean isActive;
        private LocalDateTime createdAt;

        public static Response from(Patient p) {
            Response r = new Response();
            r.setId(p.getId());
            r.setPatientCode(p.getPatientCode());
            r.setFullName(p.getFullName());
            r.setDateOfBirth(p.getDateOfBirth());
            r.setGender(p.getGender());
            r.setBloodGroup(p.getBloodGroup());
            r.setPhone(p.getPhone());
            r.setAddress(p.getAddress());
            r.setEmergencyContact(p.getEmergencyContact());
            r.setEmergencyPhone(p.getEmergencyPhone());
            r.setIsActive(p.getIsActive());
            r.setCreatedAt(p.getCreatedAt());
            if (p.getDoctor() != null) {
                r.setDoctorId(p.getDoctor().getId());
                r.setDoctorName(p.getDoctor().getFullName());
            }
            if (p.getHospital() != null) {
                r.setHospitalId(p.getHospital().getId());
                r.setHospitalName(p.getHospital().getName());
            }
            return r;
        }
    }
}
