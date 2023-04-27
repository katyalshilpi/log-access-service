package com.jpmc.accessor.logs.v1.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.jpmc.accessor.logs.v1.loggerator.LoggeratorAccessor;
import com.jpmc.accessor.logs.v1.model.JPMCLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogAccessService {
  private final LoggeratorAccessor loggeratorAccessor;

  @Autowired
  public LogAccessService(LoggeratorAccessor loggeratorAccessor) {
    this.loggeratorAccessor = loggeratorAccessor;
  }

  public List<JPMCLog> getLogs(String code, String method, String user) throws Exception {
    List<JPMCLog> logs;
    try {
      logs = loggeratorAccessor.getLogs(code, method, user);
      logs = logs.stream()
          .sorted(Comparator.comparing(JPMCLog::getDate).reversed())
          .collect(Collectors.toList());
    } catch(Exception e) {
      log.error("Error filtering and sorting log entries - " + e.getMessage());
      throw e;
    }
    return logs;
  }
}
