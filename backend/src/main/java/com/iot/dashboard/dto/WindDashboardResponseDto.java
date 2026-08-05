package com.iot.dashboard.dto;

import com.iot.dashboard.model.WindData;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class WindDashboardResponseDto {
    private List<WindData> dataList;
    private Double avgWindSpeed;
    private Double avgWindGust;
    private Double avgPressure;
    private Double avgTemperature;
    private Double avgHumidity;
}
