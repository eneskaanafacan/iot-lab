package com.iot.dashboard.repository;

import com.iot.dashboard.model.FloodData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FloodDataRepository extends MongoRepository<FloodData, String> {
    List<FloodData> findByTimestampAfter(LocalDateTime timestamp);

    List<FloodData> findByNodeIdOrderByTimestampDesc(String nodeId);

    List<FloodData> findByNodeIdAndTimestampBetweenOrderByTimestampDesc(String nodeId, LocalDateTime start, LocalDateTime end);
}
