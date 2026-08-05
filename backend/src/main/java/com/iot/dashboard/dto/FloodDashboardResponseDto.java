package com.iot.dashboard.dto;

import com.iot.dashboard.model.FloodData;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class FloodDashboardResponseDto {
    private List<FloodData> dataList;
    private Double avgRiverHeight;
    private Double avgFlowSpeed;
    private Double avgRainfall;
    private Double avgSoilMoisture;
    private Double avgFloodRiskScore;
}
