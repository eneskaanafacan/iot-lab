package com.iot.dashboard.dto;

import com.iot.dashboard.model.LiveData;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LiveDashboardResponseDto {

    private List<LiveData> dataList;

    private Double avgEnvTemp;
    private Double avgHumidity;
    private Double avgLight;
    private Double avgBattery;
    private Double avgOnChipTemp;
}
