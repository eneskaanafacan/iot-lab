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

@Document(collection = "flood_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloodData {
    @Id
    private String id;
    private String river_id;
    
    @org.springframework.data.mongodb.core.mapping.Field("node_id")
    @NotNull
    @Indexed
    private String nodeId;
    
    private Double lat;
    private Double lon;
    private Double river_height_m;
    private Double river_height_rate_cm_min;
    private Double flow_speed_mps;
    private Double rainfall_mm_h;
    private Double soil_moisture_pct;
    private String alert_level;
    private Integer flood_risk_score;
    private Double battery_v;
    private Integer rssi_dbm;
    @Indexed
    private LocalDateTime timestamp;
}
