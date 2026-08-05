package com.iot.dashboard.repository;

import com.iot.dashboard.model.IotData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IotDataRepository extends MongoRepository<IotData, String> {

    List<IotData> findByTimestampAfter(LocalDateTime timestamp);

    @org.springframework.data.mongodb.repository.Query(value = "{ 'node_id' : ?0 }", sort = "{ 'timestamp' : -1 }")
    List<IotData> findByNodeIdOrderByTimestampDesc(Long nodeId);
}
