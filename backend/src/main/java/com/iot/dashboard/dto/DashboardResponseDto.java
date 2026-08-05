package com.iot.dashboard.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DashboardResponseDto {

    private List<IotDataDto> dataList;

    private Double avgLight;
    private Double avgTemperature;
    private Double avgHumidity;
    private Double avgSoilTemperature;
}
