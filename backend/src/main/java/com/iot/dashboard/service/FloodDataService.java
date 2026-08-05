package com.iot.dashboard.service;

import com.iot.dashboard.dto.FloodDashboardResponseDto;
import com.iot.dashboard.model.FloodData;
import com.iot.dashboard.repository.FloodDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FloodDataService {

    private final FloodDataRepository floodDataRepository;

    public FloodData saveData(FloodData data) {
        data.setTimestamp(LocalDateTime.now());
        return floodDataRepository.save(data);
    }

    public FloodDashboardResponseDto getDashboardData() {
        List<FloodData> allData = floodDataRepository.findAll();
        
        LocalDateTime threeHoursAgo = LocalDateTime.now().minusHours(3);
        List<FloodData> lastThreeHoursData = floodDataRepository.findByTimestampAfter(threeHoursAgo);

        Double avgRiverHeight = lastThreeHoursData.stream().filter(d -> d.getRiver_height_m() != null)
                .mapToDouble(FloodData::getRiver_height_m).average().orElse(0.0);
        Double avgFlowSpeed = lastThreeHoursData.stream().filter(d -> d.getFlow_speed_mps() != null)
                .mapToDouble(FloodData::getFlow_speed_mps).average().orElse(0.0);
        Double avgRainfall = lastThreeHoursData.stream().filter(d -> d.getRainfall_mm_h() != null)
                .mapToDouble(FloodData::getRainfall_mm_h).average().orElse(0.0);
        Double avgSoilMoisture = lastThreeHoursData.stream().filter(d -> d.getSoil_moisture_pct() != null)
                .mapToDouble(FloodData::getSoil_moisture_pct).average().orElse(0.0);
        Double avgFloodRiskScore = lastThreeHoursData.stream().filter(d -> d.getFlood_risk_score() != null)
                .mapToDouble(d -> d.getFlood_risk_score()).average().orElse(0.0);

        return FloodDashboardResponseDto.builder()
                .dataList(allData)
                .avgRiverHeight(avgRiverHeight)
                .avgFlowSpeed(avgFlowSpeed)
                .avgRainfall(avgRainfall)
                .avgSoilMoisture(avgSoilMoisture)
                .avgFloodRiskScore(avgFloodRiskScore)
                .build();
    }

    public String exportDataToCsv() {
        List<FloodData> allData = floodDataRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "timestamp"));

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Zaman,Node ID,Nehir ID,Enlem,Boylam,Nehir Yuksekligi (m),Yukselme Hizi (cm/dk),Akis Hizi (m/s),Yagis (mm/sa),Toprak Nemi (%),Uyari Seviyesi,Sel Riski Skoru,Pil (V),Sinyal (dBm)\n");

        for (FloodData data : allData) {
            csvBuilder.append(data.getTimestamp() != null ? data.getTimestamp().toString() : "").append(",")
                      .append(data.getNodeId() != null ? data.getNodeId() : "").append(",")
                      .append(data.getRiver_id() != null ? data.getRiver_id() : "").append(",")
                      .append(data.getLat() != null ? data.getLat() : "").append(",")
                      .append(data.getLon() != null ? data.getLon() : "").append(",")
                      .append(data.getRiver_height_m() != null ? data.getRiver_height_m() : "").append(",")
                      .append(data.getRiver_height_rate_cm_min() != null ? data.getRiver_height_rate_cm_min() : "").append(",")
                      .append(data.getFlow_speed_mps() != null ? data.getFlow_speed_mps() : "").append(",")
                      .append(data.getRainfall_mm_h() != null ? data.getRainfall_mm_h() : "").append(",")
                      .append(data.getSoil_moisture_pct() != null ? data.getSoil_moisture_pct() : "").append(",")
                      .append(data.getAlert_level() != null ? data.getAlert_level() : "").append(",")
                      .append(data.getFlood_risk_score() != null ? data.getFlood_risk_score() : "").append(",")
                      .append(data.getBattery_v() != null ? data.getBattery_v() : "").append(",")
                      .append(data.getRssi_dbm() != null ? data.getRssi_dbm() : "")
                      .append("\n");
        }

        return csvBuilder.toString();
    }

    public Object getNodeDetail(String nodeId) {
        List<FloodData> allNodeData = floodDataRepository.findByNodeIdOrderByTimestampDesc(nodeId);
        
        List<FloodData> recentData = allNodeData.stream()
                .limit(50)
                .collect(java.util.stream.Collectors.toList());
                
        Double avgFlowSpeed = allNodeData.stream().filter(d -> d.getFlow_speed_mps() != null)
                .mapToDouble(FloodData::getFlow_speed_mps).average().orElse(0.0);
        Double avgRiverHeight = allNodeData.stream().filter(d -> d.getRiver_height_m() != null)
                .mapToDouble(FloodData::getRiver_height_m).average().orElse(0.0);
                
        java.util.Map<String, Object> stats = java.util.Map.of(
                "avgFlowSpeed", avgFlowSpeed,
                "avgRiverHeight", avgRiverHeight
        );

        return java.util.Map.of(
                "nodeId", nodeId,
                "recentData", recentData,
                "statistics", stats
        );
    }
}
