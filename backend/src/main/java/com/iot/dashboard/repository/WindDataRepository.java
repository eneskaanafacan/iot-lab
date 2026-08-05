package com.iot.dashboard.repository;

import com.iot.dashboard.model.WindData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WindDataRepository extends MongoRepository<WindData, String> {
    List<WindData> findByTimestampAfter(LocalDateTime timestamp);

    List<WindData> findByNodeIdOrderByTimestampDesc(String nodeId);

    List<WindData> findByNodeIdAndTimestampBetweenOrderByTimestampDesc(String nodeId, LocalDateTime start, LocalDateTime end);
}
