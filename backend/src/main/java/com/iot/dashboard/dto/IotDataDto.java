package com.iot.dashboard.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class IotDataDto {
    private String id;
    private Integer is_energest;
    private Long node_id;
    private Double light;
    private Double temperature;
    private Double humidity;
    private Double soil_temperature;
    private Integer clock_drift;
    private LocalDateTime timestamp;
}
