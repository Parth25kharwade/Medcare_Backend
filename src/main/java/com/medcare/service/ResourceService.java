package com.medcare.service;

import com.medcare.ai.ResourceStressEngine;
import com.medcare.dto.ResourceDto;
import com.medcare.entity.*;
import com.medcare.exception.ResourceNotFoundException;
import com.medcare.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ResourceService {

    private final IcuBedRepository icuBedRepository;
    private final StaffRepository staffRepository;
    private final EquipmentRepository equipmentRepository;
    private final ResourceForecastRepository forecastRepository;
    private final HospitalRepository hospitalRepository;
    private final ResourceStressEngine stressEngine;

    public ResourceService(IcuBedRepository icuBedRepository,
                           StaffRepository staffRepository,
                           EquipmentRepository equipmentRepository,
                           ResourceForecastRepository forecastRepository,
                           HospitalRepository hospitalRepository,
                           ResourceStressEngine stressEngine) {
        this.icuBedRepository = icuBedRepository;
        this.staffRepository = staffRepository;
        this.equipmentRepository = equipmentRepository;
        this.forecastRepository = forecastRepository;
        this.hospitalRepository = hospitalRepository;
        this.stressEngine = stressEngine;
    }

    @Transactional
    public ResourceDto.IcuResponse updateIcuBeds(ResourceDto.IcuUpdateRequest request) {
        Hospital hospital = getHospital(request.getHospitalId());

        IcuBed icuBed = icuBedRepository.findByHospitalId(request.getHospitalId())
                .orElse(IcuBed.builder().hospital(hospital).build());

        icuBed.setTotalBeds(request.getTotalBeds());
        icuBed.setOccupiedBeds(request.getOccupiedBeds());
        icuBedRepository.save(icuBed);

        return mapIcuToResponse(icuBed);
    }

    @Transactional
    public ResourceDto.StaffResponse updateStaff(ResourceDto.StaffUpdateRequest request) {
        Hospital hospital = getHospital(request.getHospitalId());

        Staff staff = staffRepository.findByHospitalId(request.getHospitalId())
                .stream()
                .filter(s -> s.getDepartment().equalsIgnoreCase(request.getDepartment()))
                .findFirst()
                .orElse(Staff.builder().hospital(hospital).department(request.getDepartment()).build());

        staff.setTotalStaff(request.getTotalStaff());
        staff.setOnDutyStaff(request.getOnDutyStaff());

        double workload = request.getTotalStaff() > 0
                ? ((double) request.getOnDutyStaff() / request.getTotalStaff()) * 100 : 0;
        staff.setWorkloadPct(BigDecimal.valueOf(workload));
        staffRepository.save(staff);

        return mapStaffToResponse(staff);
    }

    @Transactional
    public ResourceDto.ForecastResponse predictResourceStress(Long hospitalId) {
        Hospital hospital = getHospital(hospitalId);
        IcuBed icuBed = icuBedRepository.findByHospitalId(hospitalId).orElse(null);
        List<Staff> staffList = staffRepository.findByHospitalId(hospitalId);
        List<Equipment> equipmentList = equipmentRepository.findByHospitalId(hospitalId);

        ResourceDto.ForecastResponse forecast = stressEngine.predict(hospital, icuBed, staffList, equipmentList);

        // Persist forecast
        ResourceForecast entity = ResourceForecast.builder()
                .hospital(hospital)
                .stressLevel(ResourceForecast.StressLevel.valueOf(forecast.getStressLevel()))
                .icuStressScore(forecast.getIcuStressScore())
                .staffStressScore(forecast.getStaffStressScore())
                .equipmentStressScore(forecast.getEquipmentStressScore())
                .overallStressScore(forecast.getOverallStressScore())
                .alerts(String.join(" | ", forecast.getAlerts()))
                .recommendations(String.join(" | ", forecast.getRecommendations()))
                .build();
        forecastRepository.save(entity);

        return forecast;
    }

    @Transactional(readOnly = true)
    public ResourceDto.IcuResponse getIcuStatus(Long hospitalId) {
        IcuBed icuBed = icuBedRepository.findByHospitalId(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("ICU data not found for hospital " + hospitalId));
        return mapIcuToResponse(icuBed);
    }

    @Transactional(readOnly = true)
    public List<ResourceDto.StaffResponse> getStaffStatus(Long hospitalId) {
        return staffRepository.findByHospitalId(hospitalId)
                .stream().map(this::mapStaffToResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ResourceDto.ForecastResponse> getForecastHistory(Long hospitalId) {
        return forecastRepository.findByHospitalIdOrderByForecastDateDesc(hospitalId)
                .stream().map(this::mapForecastToResponse).toList();
    }

    private ResourceDto.IcuResponse mapIcuToResponse(IcuBed icu) {
        ResourceDto.IcuResponse r = new ResourceDto.IcuResponse();
        r.setId(icu.getId());
        r.setHospitalId(icu.getHospital().getId());
        r.setHospitalName(icu.getHospital().getName());
        r.setTotalBeds(icu.getTotalBeds());
        r.setOccupiedBeds(icu.getOccupiedBeds());
        r.setAvailableBeds(icu.getTotalBeds() - icu.getOccupiedBeds());
        double pct = icu.getTotalBeds() > 0
                ? ((double) icu.getOccupiedBeds() / icu.getTotalBeds()) * 100 : 0;
        r.setUtilizationPct(BigDecimal.valueOf(pct));
        r.setStressLevel(pct >= 90 ? "CRITICAL" : pct >= 80 ? "HIGH" : pct >= 60 ? "ELEVATED" : "NORMAL");
        return r;
    }

    private ResourceDto.StaffResponse mapStaffToResponse(Staff s) {
        ResourceDto.StaffResponse r = new ResourceDto.StaffResponse();
        r.setId(s.getId());
        r.setHospitalId(s.getHospital().getId());
        r.setDepartment(s.getDepartment());
        r.setTotalStaff(s.getTotalStaff());
        r.setOnDutyStaff(s.getOnDutyStaff());
        r.setWorkloadPct(s.getWorkloadPct());
        double wl = s.getWorkloadPct() != null ? s.getWorkloadPct().doubleValue() : 0;
        r.setStressLevel(wl >= 90 ? "CRITICAL" : wl >= 80 ? "HIGH" : wl >= 60 ? "ELEVATED" : "NORMAL");
        return r;
    }

    private ResourceDto.ForecastResponse mapForecastToResponse(ResourceForecast f) {
        ResourceDto.ForecastResponse r = new ResourceDto.ForecastResponse();
        r.setHospitalId(f.getHospital().getId());
        r.setHospitalName(f.getHospital().getName());
        r.setStressLevel(f.getStressLevel().name());
        r.setIcuStressScore(f.getIcuStressScore());
        r.setStaffStressScore(f.getStaffStressScore());
        r.setEquipmentStressScore(f.getEquipmentStressScore());
        r.setOverallStressScore(f.getOverallStressScore());
        r.setAlerts(f.getAlerts() != null ? List.of(f.getAlerts().split(" \\| ")) : List.of());
        r.setRecommendations(f.getRecommendations() != null ? List.of(f.getRecommendations().split(" \\| ")) : List.of());
        r.setForecastDate(f.getForecastDate() != null ? f.getForecastDate().toString() : null);
        return r;
    }

    private Hospital getHospital(Long hospitalId) {
        return hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found: " + hospitalId));
    }
}
