package com.iot.dashboard.controller;

import com.iot.dashboard.dto.FloodDashboardResponseDto;
import com.iot.dashboard.model.FloodData;
import com.iot.dashboard.service.FloodDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:4200", "${app.cors.allowed-origin:}"}, maxAge = 3600)
@RestController
@RequestMapping("/api/v1/flood")
@RequiredArgsConstructor
public class FloodController {

    private final FloodDataService floodDataService;

    @PostMapping("/data")
    public ResponseEntity<?> receiveFloodData(@Valid @RequestBody FloodData data) {
        FloodData savedObject = floodDataService.saveData(data);
        return ResponseEntity.ok("Flood verisi başarıyla alındı ve kaydedildi. ID: " + savedObject.getId());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<FloodDashboardResponseDto> getDashboardData() {
        return ResponseEntity.ok(floodDataService.getDashboardData());
    }

    @GetMapping(value = "/export/csv", produces = "text/csv")
    public ResponseEntity<String> exportFloodDataToCsv() {
        String csvData = floodDataService.exportDataToCsv();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"flood_sensor_data.csv\"")
                .header("Content-Type", "text/csv")
                .body(csvData);
    }

    @GetMapping("/node/{nodeId}")
    public ResponseEntity<?> getNodeDetail(@PathVariable String nodeId) {
        return ResponseEntity.ok(floodDataService.getNodeDetail(nodeId));
    }
}
