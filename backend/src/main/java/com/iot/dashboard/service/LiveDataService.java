package com.iot.dashboard.service;

import com.iot.dashboard.controller.LiveDataController;
import com.iot.dashboard.dto.LiveDashboardResponseDto;
import com.iot.dashboard.model.LiveData;
import com.iot.dashboard.repository.LiveDataRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LiveDataService {

    private final LiveDataRepository liveDataRepository;
    private final TelegramNotificationService telegramNotificationService;
    private static final Logger logger = LoggerFactory.getLogger(LiveDataService.class);

    public LiveData saveData(LiveData data) {
        data.setTimestamp(LocalDateTime.now());
        LiveData savedData = liveDataRepository.save(data);
        logger.info("Yeni canlı cihaz verisi kaydedildi. Node ID: {}", savedData.getNode_id());

        if (savedData.getEnv_temp_c() != null && savedData.getEnv_temp_c() > 40.0) {
            String alertMessage = String.format(
                    "🚨 *Canlı Sistem - Yüksek Sıcaklık Uyarısı!* 🚨\nNode ID: %d\nÖlçülen Sıcaklık: %.2f °C\nNem: %.2f %%RH\nSaat: %s\nLütfen cihazı kontrol ediniz.",
                    savedData.getNode_id(),
                    savedData.getEnv_temp_c(),
                    savedData.getHumidity_rh(),
                    savedData.getTimestamp().toString()
            );
            telegramNotificationService.sendMessage(alertMessage);
        }

        for (SseEmitter emitter : LiveDataController.emitters) {
            try {
                emitter.send(savedData);
            } catch (Exception e) {
                LiveDataController.emitters.remove(emitter);
            }
        }

        return savedData;
    }

    public LiveDashboardResponseDto getDashboardData() {
        List<LiveData> allData = liveDataRepository.findAll();

        LocalDateTime threeHoursAgo = LocalDateTime.now().minusHours(3);
        List<LiveData> lastThreeHoursData = liveDataRepository.findByTimestampAfter(threeHoursAgo);

        Double avgEnvTemp = lastThreeHoursData.stream().filter(d -> d.getEnv_temp_c() != null)
                .mapToDouble(LiveData::getEnv_temp_c).average().orElse(0.0);

        Double avgHumidity = lastThreeHoursData.stream().filter(d -> d.getHumidity_rh() != null)
                .mapToDouble(LiveData::getHumidity_rh).average().orElse(0.0);

        Double avgLight = lastThreeHoursData.stream().filter(d -> d.getLight_lux() != null)
                .mapToDouble(LiveData::getLight_lux).average().orElse(0.0);

        Double avgBattery = lastThreeHoursData.stream().filter(d -> d.getBattery_mv() != null)
                .mapToDouble(LiveData::getBattery_mv).average().orElse(0.0);

        Double avgOnChipTemp = lastThreeHoursData.stream().filter(d -> d.getOnchip_temp_c() != null)
                .mapToDouble(LiveData::getOnchip_temp_c).average().orElse(0.0);

        return LiveDashboardResponseDto.builder()
                .dataList(allData)
                .avgEnvTemp(avgEnvTemp)
                .avgHumidity(avgHumidity)
                .avgLight(avgLight)
                .avgBattery(avgBattery)
                .avgOnChipTemp(avgOnChipTemp)
                .build();
    }

    public String exportDataToCsv() {
        List<LiveData> allData = liveDataRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "timestamp"));

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Zaman,Node ID,Uptime (s),Cip Sicakligi (C),Pil (mV),Ortam Sicakligi (C),Nem (%RH),Isik (Lux)\n");

        for (LiveData data : allData) {
            csvBuilder.append(data.getTimestamp() != null ? data.getTimestamp().toString() : "").append(",")
                      .append(data.getNode_id() != null ? data.getNode_id() : "").append(",")
                      .append(data.getTime_s() != null ? data.getTime_s() : "").append(",")
                      .append(data.getOnchip_temp_c() != null ? data.getOnchip_temp_c() : "").append(",")
                      .append(data.getBattery_mv() != null ? data.getBattery_mv() : "").append(",")
                      .append(data.getEnv_temp_c() != null ? data.getEnv_temp_c() : "").append(",")
                      .append(data.getHumidity_rh() != null ? data.getHumidity_rh() : "").append(",")
                      .append(data.getLight_lux() != null ? data.getLight_lux() : "")
                      .append("\n");
        }

        return csvBuilder.toString();
    }

    public Object getNodeDetail(Long nodeId) {
        List<LiveData> allNodeData = liveDataRepository.findByNodeIdOrderByTimestampDesc(nodeId);

        List<LiveData> recentData = allNodeData.stream()
                .limit(50)
                .collect(Collectors.toList());

        Double avgEnvTemp = allNodeData.stream().filter(d -> d.getEnv_temp_c() != null)
                .mapToDouble(LiveData::getEnv_temp_c).average().orElse(0.0);

        Double avgHumidity = allNodeData.stream().filter(d -> d.getHumidity_rh() != null)
                .mapToDouble(LiveData::getHumidity_rh).average().orElse(0.0);

        Double avgLight = allNodeData.stream().filter(d -> d.getLight_lux() != null)
                .mapToDouble(LiveData::getLight_lux).average().orElse(0.0);

        Double avgBattery = allNodeData.stream().filter(d -> d.getBattery_mv() != null)
                .mapToDouble(LiveData::getBattery_mv).average().orElse(0.0);

        Double avgOnChipTemp = allNodeData.stream().filter(d -> d.getOnchip_temp_c() != null)
                .mapToDouble(LiveData::getOnchip_temp_c).average().orElse(0.0);

        java.util.Map<String, Object> stats = java.util.Map.of(
                "avgEnvTemp", avgEnvTemp,
                "avgHumidity", avgHumidity,
                "avgLight", avgLight,
                "avgBattery", avgBattery,
                "avgOnChipTemp", avgOnChipTemp
        );

        return java.util.Map.of(
                "nodeId", nodeId,
                "recentData", recentData,
                "statistics", stats
        );
    }
}
