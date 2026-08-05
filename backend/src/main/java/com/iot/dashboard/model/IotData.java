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

@Document(collection = "iot_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotData {

    @Id
    private String id;

    private Integer is_energest;

    @NotNull
    @Indexed
    private Long node_id;

    private Double light;

    private Double temperature;

    private Double humidity;

    private Double soil_temperature;

    private Integer clock_drift;

    @Indexed
    private LocalDateTime timestamp;
}
