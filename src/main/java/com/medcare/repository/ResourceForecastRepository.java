package com.medcare.repository;

import com.medcare.entity.ResourceForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResourceForecastRepository extends JpaRepository<ResourceForecast, Long> {
    List<ResourceForecast> findByHospitalIdOrderByForecastDateDesc(Long hospitalId);
    List<ResourceForecast> findByStressLevelIn(List<ResourceForecast.StressLevel> levels);
}
