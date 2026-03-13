package com.medcare.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

public class ResourceDto {

    @Data
    public static class IcuUpdateRequest {
        @NotNull
        private Long hospitalId;
        @Min(0)
        private Integer totalBeds;
        @Min(0)
        private Integer occupiedBeds;
    }

    @Data
    public static class StaffUpdateRequest {
        @NotNull
        private Long hospitalId;
        @NotNull
        private String department;
        @Min(0)
        private Integer totalStaff;
        @Min(0)
        private Integer onDutyStaff;
    }

    @Data
    public static class EquipmentRequest {
        @NotNull
        private Long hospitalId;
        @NotNull
        private String equipmentName;
        @Min(0)
        private Integer totalUnits;
        @Min(0)
        private Integer inUseUnits;
    }

    @Data
    public static class IcuResponse {
        private Long id;
        private Long hospitalId;
        private String hospitalName;
        private Integer totalBeds;
        private Integer occupiedBeds;
        private Integer availableBeds;
        private BigDecimal utilizationPct;
        private String stressLevel;
    }

    @Data
    public static class StaffResponse {
        private Long id;
        private Long hospitalId;
        private String department;
        private Integer totalStaff;
        private Integer onDutyStaff;
        private BigDecimal workloadPct;
        private String stressLevel;
    }

    @Data
    public static class ForecastResponse {
        private Long hospitalId;
        private String hospitalName;
        private String stressLevel;
        private BigDecimal icuStressScore;
        private BigDecimal staffStressScore;
        private BigDecimal equipmentStressScore;
        private BigDecimal overallStressScore;
        private List<String> alerts;
        private List<String> recommendations;
        private String forecastDate;
    }
}
