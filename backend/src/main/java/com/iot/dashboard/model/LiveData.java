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

@Document(collection = "live_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveData {

    @Id
    private String id;

    @NotNull
    @Indexed
    private Long node_id;

    private Long time_s;

    private Integer onchip_temp_c;

    private Integer battery_mv;

    private Double env_temp_c;

    private Double humidity_rh;

    private Double light_lux;

    @Indexed
    private LocalDateTime timestamp;
}
