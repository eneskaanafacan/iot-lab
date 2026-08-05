package com.iot.dashboard.controller;

import com.iot.dashboard.dto.DashboardResponseDto;
import com.iot.dashboard.dto.NodeDetailResponseDto;
import com.iot.dashboard.model.IotData;
import com.iot.dashboard.service.IotDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:4200", "${app.cors.allowed-origin:}"}, maxAge = 3600)
@RestController
@RequestMapping("/api/v1/iot")
@RequiredArgsConstructor
public class IotDataController {

    private final IotDataService iotDataService;

    @PostMapping("/data")
    public ResponseEntity<?> receiveIotData(@Valid @RequestBody IotData iotData) {
        IotData savedObject = iotDataService.saveData(iotData);
        return ResponseEntity.ok("IoT verisi başarıyla alındı ve kaydedildi. ID: " + savedObject.getId());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponseDto> getDashboardData() {
        DashboardResponseDto dashboardData = iotDataService.getDashboardData();
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping(value = "/export/csv", produces = "text/csv")
    public ResponseEntity<String> exportIotDataToCsv() {
        String csvData = iotDataService.exportIotDataToCsv();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"iot_sensor_data.csv\"")
                .header("Content-Type", "text/csv")
                .body(csvData);
    }

    @GetMapping("/node/{nodeId}")
    public ResponseEntity<NodeDetailResponseDto> getNodeDetail(@PathVariable Long nodeId) {
        NodeDetailResponseDto nodeDetail = iotDataService.getNodeDetail(nodeId);
        return ResponseEntity.ok(nodeDetail);
    }
}
