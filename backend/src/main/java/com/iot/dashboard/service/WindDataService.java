package com.iot.dashboard.service;

import com.iot.dashboard.dto.WindDashboardResponseDto;
import com.iot.dashboard.model.WindData;
import com.iot.dashboard.repository.WindDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WindDataService {

    private final WindDataRepository windDataRepository;

    public WindData saveData(WindData data) {
        data.setTimestamp(LocalDateTime.now());
        return windDataRepository.save(data);
    }

    public WindDashboardResponseDto getDashboardData() {
        List<WindData> allData = windDataRepository.findAll();
        
        LocalDateTime threeHoursAgo = LocalDateTime.now().minusHours(3);
        List<WindData> lastThreeHoursData = windDataRepository.findByTimestampAfter(threeHoursAgo);

        Double avgWindSpeed = lastThreeHoursData.stream().filter(d -> d.getWind_speed_mps() != null)
                .mapToDouble(WindData::getWind_speed_mps).average().orElse(0.0);
        Double avgWindGust = lastThreeHoursData.stream().filter(d -> d.getWind_gust_mps() != null)
                .mapToDouble(WindData::getWind_gust_mps).average().orElse(0.0);
        Double avgPressure = lastThreeHoursData.stream().filter(d -> d.getPressure_hpa() != null)
                .mapToDouble(WindData::getPressure_hpa).average().orElse(0.0);
        Double avgTemp = lastThreeHoursData.stream().filter(d -> d.getTemperature_c() != null)
                .mapToDouble(WindData::getTemperature_c).average().orElse(0.0);
        Double avgHum = lastThreeHoursData.stream().filter(d -> d.getHumidity_pct() != null)
                .mapToDouble(WindData::getHumidity_pct).average().orElse(0.0);

        return WindDashboardResponseDto.builder()
                .dataList(allData)
                .avgWindSpeed(avgWindSpeed)
                .avgWindGust(avgWindGust)
                .avgPressure(avgPressure)
                .avgTemperature(avgTemp)
                .avgHumidity(avgHum)
                .build();
    }

    public String exportDataToCsv() {
        List<WindData> allData = windDataRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "timestamp"));

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Zaman,Node ID,Liman ID,Enlem,Boylam,Ruzgar Hizi (m/s),Ruzgar Yonu (derece),Ruzgar Hamlesi (m/s),Basinc (hPa),Sicaklik (C),Nem (%),Pil (V),Sinyal (dBm),Baglanti Kalitesi\n");

        for (WindData data : allData) {
            csvBuilder.append(data.getTimestamp() != null ? data.getTimestamp().toString() : "").append(",")
                      .append(data.getNodeId() != null ? data.getNodeId() : "").append(",")
                      .append(data.getHarbour_id() != null ? data.getHarbour_id() : "").append(",")
                      .append(data.getLat() != null ? data.getLat() : "").append(",")
                      .append(data.getLon() != null ? data.getLon() : "").append(",")
                      .append(data.getWind_speed_mps() != null ? data.getWind_speed_mps() : "").append(",")
                      .append(data.getWind_direction_deg() != null ? data.getWind_direction_deg() : "").append(",")
                      .append(data.getWind_gust_mps() != null ? data.getWind_gust_mps() : "").append(",")
                      .append(data.getPressure_hpa() != null ? data.getPressure_hpa() : "").append(",")
                      .append(data.getTemperature_c() != null ? data.getTemperature_c() : "").append(",")
                      .append(data.getHumidity_pct() != null ? data.getHumidity_pct() : "").append(",")
                      .append(data.getBattery_v() != null ? data.getBattery_v() : "").append(",")
                      .append(data.getRssi_dbm() != null ? data.getRssi_dbm() : "").append(",")
                      .append(data.getLink_quality() != null ? data.getLink_quality() : "")
                      .append("\n");
        }

        return csvBuilder.toString();
    }

    public Object getNodeDetail(String nodeId) {
        List<WindData> allNodeData = windDataRepository.findByNodeIdOrderByTimestampDesc(nodeId);
        
        List<WindData> recentData = allNodeData.stream()
                .limit(50)
                .collect(java.util.stream.Collectors.toList());
                
        Double avgTemp = allNodeData.stream().filter(d -> d.getTemperature_c() != null)
                .mapToDouble(WindData::getTemperature_c).average().orElse(0.0);
        Double avgHum = allNodeData.stream().filter(d -> d.getHumidity_pct() != null)
                .mapToDouble(WindData::getHumidity_pct).average().orElse(0.0);
                
        java.util.Map<String, Object> stats = java.util.Map.of(
                "avgTemperature", avgTemp,
                "avgHumidity", avgHum
        );

        return java.util.Map.of(
                "nodeId", nodeId,
                "recentData", recentData,
                "statistics", stats
        );
    }
}
