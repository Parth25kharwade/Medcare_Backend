package com.medcare.service;

import com.medcare.dto.PatientDto;
import com.medcare.entity.*;
import com.medcare.exception.ResourceNotFoundException;
import com.medcare.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;

    public PatientService(PatientRepository patientRepository,
                          UserRepository userRepository,
                          HospitalRepository hospitalRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.hospitalRepository = hospitalRepository;
    }

    @Transactional
    public PatientDto.Response createPatient(PatientDto.CreateRequest request) {
        Patient patient = new Patient();
        patient.setPatientCode(generatePatientCode());
        patient.setFullName(request.getFullName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setPhone(request.getPhone());
        patient.setAddress(request.getAddress());
        patient.setEmergencyContact(request.getEmergencyContact());
        patient.setEmergencyPhone(request.getEmergencyPhone());
        patient.setIsActive(true);

        if (request.getDoctorId() != null) {
            User doctor = userRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            patient.setDoctor(doctor);
        }
        if (request.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
            patient.setHospital(hospital);
        }

        return PatientDto.Response.from(patientRepository.save(patient));
    }

    @Transactional
    public PatientDto.Response updatePatient(Long id, PatientDto.UpdateRequest request) {
        Patient patient = getPatientEntity(id);

        if (request.getFullName() != null) patient.setFullName(request.getFullName());
        if (request.getDateOfBirth() != null) patient.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) patient.setGender(request.getGender());
        if (request.getBloodGroup() != null) patient.setBloodGroup(request.getBloodGroup());
        if (request.getPhone() != null) patient.setPhone(request.getPhone());
        if (request.getAddress() != null) patient.setAddress(request.getAddress());
        if (request.getEmergencyContact() != null) patient.setEmergencyContact(request.getEmergencyContact());
        if (request.getEmergencyPhone() != null) patient.setEmergencyPhone(request.getEmergencyPhone());

        if (request.getDoctorId() != null) {
            User doctor = userRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            patient.setDoctor(doctor);
        }

        return PatientDto.Response.from(patientRepository.save(patient));
    }

    @Transactional(readOnly = true)
    public PatientDto.Response getPatient(Long id) {
        return PatientDto.Response.from(getPatientEntity(id));
    }

    @Transactional(readOnly = true)
    public List<PatientDto.Response> getPatientsByDoctor(Long doctorId) {
        return patientRepository.findByDoctorIdAndIsActiveTrue(doctorId)
                .stream().map(PatientDto.Response::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PatientDto.Response> getPatientsByHospital(Long hospitalId) {
        return patientRepository.findByHospitalIdAndIsActiveTrue(hospitalId)
                .stream().map(PatientDto.Response::from).toList();
    }

    @Transactional
    public void deactivatePatient(Long id) {
        Patient patient = getPatientEntity(id);
        patient.setIsActive(false);
        patientRepository.save(patient);
    }

    public Patient getPatientEntity(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    private String generatePatientCode() {
        String prefix = "PAT-";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + timestamp + "-" + suffix;
    }
}
