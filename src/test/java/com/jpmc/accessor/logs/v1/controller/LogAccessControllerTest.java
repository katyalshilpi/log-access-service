package com.jpmc.accessor.logs.v1.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import java.util.LinkedList;
import java.util.List;

import com.jpmc.accessor.logs.v1.model.JPMCLog;
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
  private final JPMCLog jpmcLog1 = new JPMCLog("23.59.50.157", "-", "annstewart", "[18/Jul/2000 02:12:31 +0000]",
                                               "\"POST /photos/90 HTTP/1.0\"", "200", 97);
  private final List<JPMCLog> logResponse = new LinkedList<>();

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    logAccessController = new LogAccessController(mockLogAccessService);
    logResponse.add(jpmcLog1);
  }

  @Test
  public void testGetLogs() throws Exception {
    when(mockLogAccessService.getLogs("200", "POST", "annstewart"))
        .thenReturn(logResponse);

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
