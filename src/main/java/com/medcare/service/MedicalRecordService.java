package com.medcare.service;

import com.medcare.dto.MedicalRecordDto;
import com.medcare.entity.*;
import com.medcare.exception.ResourceNotFoundException;
import com.medcare.repository.*;
import com.medcare.security.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepository;
    private final PatientService patientService;
    private final UserRepository userRepository;

    public MedicalRecordService(MedicalRecordRepository recordRepository,
                                PatientService patientService,
                                UserRepository userRepository) {
        this.recordRepository = recordRepository;
        this.patientService = patientService;
        this.userRepository = userRepository;
    }

    @Transactional
    public MedicalRecordDto.Response createRecord(MedicalRecordDto.CreateRequest request) {
        Patient patient = patientService.getPatientEntity(request.getPatientId());

        MedicalRecord record = MedicalRecord.builder()
                .patient(patient)
                .recordType(request.getRecordType())
                .title(request.getTitle())
                .description(request.getDescription())
                .diagnosis(request.getDiagnosis())
                .treatment(request.getTreatment())
                .medications(request.getMedications())
                .allergies(request.getAllergies())
                .visitDate(request.getVisitDate())
                .build();

        try {
            UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            userRepository.findById(principal.getId()).ifPresent(record::setDoctor);
        } catch (Exception ignored) {}

        return mapToResponse(recordRepository.save(record));
    }

    @Transactional
    public MedicalRecordDto.Response uploadFile(Long recordId, MultipartFile file) throws IOException {
        MedicalRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));

        String uploadDir = "uploads/medical-records/";
        Files.createDirectories(Paths.get(uploadDir));
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        record.setFilePath(filePath.toString());
        return mapToResponse(recordRepository.save(record));
    }

    @Transactional(readOnly = true)
    public List<MedicalRecordDto.Response> getPatientHistory(Long patientId) {
        return recordRepository.findByPatientIdOrderByVisitDateDesc(patientId)
                .stream().map(this::mapToResponse).toList();
    }

    private MedicalRecordDto.Response mapToResponse(MedicalRecord r) {
        MedicalRecordDto.Response dto = new MedicalRecordDto.Response();
        dto.setId(r.getId());
        dto.setPatientId(r.getPatient().getId());
        dto.setPatientName(r.getPatient().getFullName());
        dto.setRecordType(r.getRecordType());
        dto.setTitle(r.getTitle());
        dto.setDescription(r.getDescription());
        dto.setDiagnosis(r.getDiagnosis());
        dto.setTreatment(r.getTreatment());
        dto.setMedications(r.getMedications());
        dto.setAllergies(r.getAllergies());
        dto.setFilePath(r.getFilePath());
        dto.setVisitDate(r.getVisitDate());
        dto.setCreatedAt(r.getCreatedAt());
        if (r.getDoctor() != null) dto.setDoctorName(r.getDoctor().getFullName());
        return dto;
    }
}
