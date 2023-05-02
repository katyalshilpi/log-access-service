package com.jpmc.accessor.logs.v1.service.kafka;

import com.jpmc.accessor.logs.v1.model.LogEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducer {

  private static final String logTopic = "jpmcLogs";

  @Autowired
  KafkaTemplate<String, String> kafkaTemplate;

  public void publish(LogEntry logEntry) {
    log.info("Publishing message - " + logEntry.toString() + " to topic - " + logTopic);
    kafkaTemplate.send(logTopic, logEntry.toString());
  }

}
