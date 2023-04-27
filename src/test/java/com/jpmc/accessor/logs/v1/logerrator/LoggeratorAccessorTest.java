package com.jpmc.accessor.logs.v1.logerrator;

import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.List;

import com.jpmc.accessor.logs.v1.loggerator.LoggeratorAccessor;
import com.jpmc.accessor.logs.v1.model.JPMCLog;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class LoggeratorAccessorTest {

  LoggeratorAccessor loggeratorAccessor;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    loggeratorAccessor = new LoggeratorAccessor();
    loggeratorAccessor.setHost("localhost");
    loggeratorAccessor.setPort(8080);
    loggeratorAccessor.setCount(10);
    loggeratorAccessor.setSeed(3);
  }

  @Test
  public void testWithNone() throws Exception {
    String code = null, user = null, method = null;
    List<JPMCLog> logList = loggeratorAccessor.getLogs(code, method, user);
    assertEquals("Result not returned.", (logList != null && logList.size() > 0), true);
  }

}
