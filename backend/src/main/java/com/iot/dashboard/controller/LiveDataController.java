package com.iot.dashboard.controller;

import com.iot.dashboard.dto.LiveDashboardResponseDto;
import com.iot.dashboard.model.LiveData;
import com.iot.dashboard.service.LiveDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@CrossOrigin(origins = {"http://localhost:4200", "${app.cors.allowed-origin:}"}, maxAge = 3600)
@RestController
@RequestMapping("/api/v1/live")
@RequiredArgsConstructor
public class LiveDataController {

    private final LiveDataService liveDataService;

    public static final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLiveData() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        return emitter;
    }

    @PostMapping("/data")
    public ResponseEntity<?> receiveLiveData(@Valid @RequestBody LiveData data) {
        LiveData savedObject = liveDataService.saveData(data);
        return ResponseEntity.ok("Canlı sensör verisi başarıyla alındı ve kaydedildi. ID: " + savedObject.getId());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<LiveDashboardResponseDto> getDashboardData() {
        LiveDashboardResponseDto dashboardData = liveDataService.getDashboardData();
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping(value = "/export/csv", produces = "text/csv")
    public ResponseEntity<String> exportLiveDataToCsv() {
        String csvData = liveDataService.exportDataToCsv();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"live_sensor_data.csv\"")
                .header("Content-Type", "text/csv")
                .body(csvData);
    }

    @GetMapping("/node/{nodeId}")
    public ResponseEntity<?> getNodeDetail(@PathVariable Long nodeId) {
        Object nodeDetail = liveDataService.getNodeDetail(nodeId);
        return ResponseEntity.ok(nodeDetail);
    }
}
