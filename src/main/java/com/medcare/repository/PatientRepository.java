package com.medcare.repository;

import com.medcare.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPatientCode(String patientCode);
    List<Patient> findByDoctorIdAndIsActiveTrue(Long doctorId);
    List<Patient> findByHospitalIdAndIsActiveTrue(Long hospitalId);
    boolean existsByPatientCode(String patientCode);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.hospital.id = :hospitalId AND p.isActive = true")
    long countActivePatientsByHospital(Long hospitalId);
}
