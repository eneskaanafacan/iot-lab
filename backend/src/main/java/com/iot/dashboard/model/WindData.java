package com.iot.dashboard.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "wind_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WindData {
    @Id
    private String id;
    private String harbour_id;
    
    @org.springframework.data.mongodb.core.mapping.Field("node_id")
    @NotNull
    @Indexed
    private String nodeId;
    
    private Double lat;
    private Double lon;
    private Double wind_speed_mps;
    private Integer wind_direction_deg;
    private Double wind_gust_mps;
    private Double pressure_hpa;
    private Double temperature_c;
    private Double humidity_pct;
    private Double battery_v;
    private Integer rssi_dbm;
    private Integer link_quality;
    @Indexed
    private LocalDateTime timestamp;
}
