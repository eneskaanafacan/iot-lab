package com.iot.dashboard.controller;

import com.iot.dashboard.dto.WindDashboardResponseDto;
import com.iot.dashboard.model.WindData;
import com.iot.dashboard.service.WindDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:4200", "${app.cors.allowed-origin:}"}, maxAge = 3600)
@RestController
@RequestMapping("/api/v1/wind")
@RequiredArgsConstructor
public class WindController {

    private final WindDataService windDataService;

    @PostMapping("/data")
    public ResponseEntity<?> receiveWindData(@Valid @RequestBody WindData data) {
        WindData savedObject = windDataService.saveData(data);
        return ResponseEntity.ok("Wind verisi başarıyla alındı ve kaydedildi. ID: " + savedObject.getId());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<WindDashboardResponseDto> getDashboardData() {
        return ResponseEntity.ok(windDataService.getDashboardData());
    }

    @GetMapping(value = "/export/csv", produces = "text/csv")
    public ResponseEntity<String> exportWindDataToCsv() {
        String csvData = windDataService.exportDataToCsv();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"wind_sensor_data.csv\"")
                .header("Content-Type", "text/csv")
                .body(csvData);
    }

    @GetMapping("/node/{nodeId}")
    public ResponseEntity<?> getNodeDetail(@PathVariable String nodeId) {
        return ResponseEntity.ok(windDataService.getNodeDetail(nodeId));
    }
}
