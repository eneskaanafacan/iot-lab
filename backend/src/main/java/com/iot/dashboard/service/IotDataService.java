package com.iot.dashboard.service;

import com.iot.dashboard.dto.DashboardResponseDto;
import com.iot.dashboard.dto.IotDataDto;
import com.iot.dashboard.dto.NodeDetailResponseDto;
import com.iot.dashboard.model.IotData;
import com.iot.dashboard.repository.IotDataRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IotDataService {

    private final IotDataRepository iotDataRepository;
    private final TelegramNotificationService telegramNotificationService;
    private static final Logger logger = LoggerFactory.getLogger(IotDataService.class);

    public IotData saveData(IotData data) {
        data.setTimestamp(LocalDateTime.now());
        
        IotData savedData = iotDataRepository.save(data);
        logger.info("Yeni cihaz verisi kaydedildi. Node ID: {}", savedData.getNode_id());

        if (savedData.getTemperature() != null && savedData.getTemperature() > 40.0) {
            String alertMessage = String.format(
                    "🚨 *Yüksek Sıcaklık Uyarısı!* 🚨\nNode ID: %d\nÖlçülen Sıcaklık: %.2f °C\nSaat: %s\nLütfen cihazı kontrol ediniz.",
                    savedData.getNode_id(),
                    savedData.getTemperature(),
                    savedData.getTimestamp().toString()
            );
            telegramNotificationService.sendMessage(alertMessage);
        }

        return savedData;
    }

    public DashboardResponseDto getDashboardData() {
        List<IotData> allData = iotDataRepository.findAll();
        
        List<IotDataDto> dtoList = allData.stream().map(this::mapToDto).collect(Collectors.toList());

        LocalDateTime threeHoursAgo = LocalDateTime.now().minusHours(3);
        List<IotData> lastThreeHoursData = iotDataRepository.findByTimestampAfter(threeHoursAgo);

        Double avgTemp = lastThreeHoursData.stream().filter(d -> d.getTemperature() != null)
                .mapToDouble(IotData::getTemperature).average().orElse(0.0);
                
        Double avgHum = lastThreeHoursData.stream().filter(d -> d.getHumidity() != null)
                .mapToDouble(IotData::getHumidity).average().orElse(0.0);
                
        Double avgLight = lastThreeHoursData.stream().filter(d -> d.getLight() != null)
                .mapToDouble(IotData::getLight).average().orElse(0.0);
                
        Double avgSoilTemp = lastThreeHoursData.stream().filter(d -> d.getSoil_temperature() != null)
                .mapToDouble(IotData::getSoil_temperature).average().orElse(0.0);

        return DashboardResponseDto.builder()
                .dataList(dtoList)
                .avgTemperature(avgTemp)
                .avgHumidity(avgHum)
                .avgLight(avgLight)
                .avgSoilTemperature(avgSoilTemp)
                .build();
    }

    private IotDataDto mapToDto(IotData entity) {
        return IotDataDto.builder()
                .id(entity.getId())
                .is_energest(entity.getIs_energest())
                .node_id(entity.getNode_id())
                .light(entity.getLight())
                .temperature(entity.getTemperature())
                .humidity(entity.getHumidity())
                .soil_temperature(entity.getSoil_temperature())
                .clock_drift(entity.getClock_drift())
                .timestamp(entity.getTimestamp())
                .build();
    }

    public String exportIotDataToCsv() {
        List<IotData> allData = iotDataRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "timestamp"));

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Zaman,Node ID,Sıcaklık,Nem,Işık,Toprak Sıcaklığı,Saat Kayması\n");
        
        for (IotData data : allData) {
            csvBuilder.append(data.getTimestamp() != null ? data.getTimestamp().toString() : "").append(",")
                      .append(data.getNode_id() != null ? data.getNode_id() : "").append(",")
                      .append(data.getTemperature() != null ? data.getTemperature() : "").append(",")
                      .append(data.getHumidity() != null ? data.getHumidity() : "").append(",")
                      .append(data.getLight() != null ? data.getLight() : "").append(",")
                      .append(data.getSoil_temperature() != null ? data.getSoil_temperature() : "").append(",")
                      .append(data.getClock_drift() != null ? data.getClock_drift() : "")
                      .append("\n");
        }
        
        return csvBuilder.toString();
    }

    public NodeDetailResponseDto getNodeDetail(Long nodeId) {
        List<IotData> allNodeData = iotDataRepository.findByNodeIdOrderByTimestampDesc(nodeId);

        List<IotData> recentData = allNodeData.stream()
                .limit(50)
                .collect(Collectors.toList());
                
        List<IotDataDto> recentDataDtos = recentData.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        Double avgTemp = allNodeData.stream().filter(d -> d.getTemperature() != null)
                .mapToDouble(IotData::getTemperature).average().orElse(0.0);
        
        Double avgHum = allNodeData.stream().filter(d -> d.getHumidity() != null)
                .mapToDouble(IotData::getHumidity).average().orElse(0.0);
                
        Double avgLight = allNodeData.stream().filter(d -> d.getLight() != null)
                .mapToDouble(IotData::getLight).average().orElse(0.0);
                
        Double avgSoilTemp = allNodeData.stream().filter(d -> d.getSoil_temperature() != null)
                .mapToDouble(IotData::getSoil_temperature).average().orElse(0.0);

        Double maxTemp = allNodeData.stream().filter(d -> d.getTemperature() != null)
                .mapToDouble(IotData::getTemperature).max().orElse(0.0);
                
        Double minTemp = allNodeData.stream().filter(d -> d.getTemperature() != null)
                .mapToDouble(IotData::getTemperature).min().orElse(0.0);

        NodeDetailResponseDto.NodeStatistics stats = NodeDetailResponseDto.NodeStatistics.builder()
                .avgTemperature(avgTemp)
                .avgHumidity(avgHum)
                .avgLight(avgLight)
                .avgSoilTemperature(avgSoilTemp)
                .maxTemperature(maxTemp)
                .minTemperature(minTemp)
                .build();

        return NodeDetailResponseDto.builder()
                .nodeId(nodeId)
                .recentData(recentDataDtos)
                .statistics(stats)
                .build();
    }
}
