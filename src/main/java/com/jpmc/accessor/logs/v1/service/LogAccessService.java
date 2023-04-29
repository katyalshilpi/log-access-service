package com.jpmc.accessor.logs.v1.service;

import java.util.Set;

import com.jpmc.accessor.logs.v1.loggerator.LoggeratorAccessor;
import com.jpmc.accessor.logs.v1.model.LogEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogAccessService {

  private final LoggeratorAccessor loggeratorAccessor;

  @Autowired
  public LogAccessService(LoggeratorAccessor loggeratorAccessor) {
    this.loggeratorAccessor = loggeratorAccessor;
  }

  public Set<LogEntry> getLogs(String code, String method, String user) throws Exception {
    return loggeratorAccessor.getLogs(code, method, user);
  }
}
