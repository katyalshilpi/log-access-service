package com.jpmc.accessor.logs.v1.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import java.util.Set;
import java.util.TreeSet;

import com.jpmc.accessor.logs.v1.model.LogEntry;
import com.jpmc.accessor.logs.v1.service.LogAccessService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public class LogAccessControllerTest {

  @Mock
  LogAccessService mockLogAccessService;

  private LogAccessController logAccessController;
  private final LogEntry logEntry1 = new LogEntry("23.59.50.157", "-", "annstewart", "[18/Jul/2000 02:12:31 +0000]", "\"POST /photos/90 HTTP/1.0\"", "200", "97");
  private final Set<LogEntry> logResponse = new TreeSet<>();

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    logAccessController = new LogAccessController(mockLogAccessService);
    logResponse.add(logEntry1);
  }

  @Test
  public void testGetLogs() throws Exception {
    when(mockLogAccessService.getLogs("200", "POST", "annstewart")).thenReturn(logResponse);

    ResponseEntity<StreamingResponseBody> resp = logAccessController.streamLogs("200", "POST", "annstewart");
    assertTrue("Result null", resp != null && resp.hasBody());
    assertTrue("Result null", resp != null && resp.getStatusCode() == HttpStatus.OK);
  }

  @Test
  public void testGetLogsNoInput() throws Exception {
    when(mockLogAccessService.getLogs(null, null, null)).thenReturn(logResponse);

    ResponseEntity<StreamingResponseBody> resp = logAccessController.streamLogs(null, null, null);
    assertTrue("Result null", resp != null);
  }

}
