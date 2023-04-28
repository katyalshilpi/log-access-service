package com.jpmc.accessor.logs.v1.service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
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

  public Set<JPMCLog> getLogs(String code, String method, String user) throws Exception {
    return loggeratorAccessor.getLogs(code, method, user);
  }
}
