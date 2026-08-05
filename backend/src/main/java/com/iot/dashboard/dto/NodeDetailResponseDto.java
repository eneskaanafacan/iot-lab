package com.iot.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeDetailResponseDto {

    private Long nodeId;
    private List<IotDataDto> recentData;
    private NodeStatistics statistics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeStatistics {
        private Double avgTemperature;
        private Double avgHumidity;
        private Double avgLight;
        private Double avgSoilTemperature;
        private Double maxTemperature;
        private Double minTemperature;
    }
}
