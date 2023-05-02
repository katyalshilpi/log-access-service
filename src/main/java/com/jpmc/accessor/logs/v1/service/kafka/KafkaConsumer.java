package com.jpmc.accessor.logs.v1.service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumer {
  private static final String logTopic = "jpmcLogs";

  @KafkaListener(topics = logTopic, groupId = "jpmcGroup")
  public void consume(String logMsg) {
    log.info("Consumed message - " + logMsg + " from topic - " + logTopic);
  }

}
