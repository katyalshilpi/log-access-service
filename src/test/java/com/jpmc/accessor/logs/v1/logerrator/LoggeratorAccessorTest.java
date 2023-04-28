package com.jpmc.accessor.logs.v1.logerrator;

import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.List;
import java.util.Set;

import com.jpmc.accessor.logs.v1.loggerator.LoggeratorAccessor;
import com.jpmc.accessor.logs.v1.model.JPMCLog;
import org.assertj.core.util.Strings;
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

  public void assertResults(String code, String method, String user) throws Exception {
    Set<JPMCLog> logList = loggeratorAccessor.getLogs(code, method, user);
    assertEquals("Result not returned.", (logList != null), true);
    if (logList != null && logList.size() > 0) {
      JPMCLog log = logList.iterator().next();
      if (!Strings.isNullOrEmpty(code)) {
        assertEquals("Log result Code does not match", code, log.getStatus());
      }
      if (!Strings.isNullOrEmpty(user)) {
        assertEquals("Log result User does not match", user, log.getAuthUser());
      }
      if (!Strings.isNullOrEmpty(method)) {
        assertEquals("Log result Method does not match", log.getRequest().toUpperCase().startsWith("\"" + method.toUpperCase()), true);
      }
    }
  }

  @Test
  public void testWithNone() throws Exception {
    String code = null, user = null, method = null;
    assertResults(code, method, user);
  }

  @Test
  public void testWithCodeUserMethod() throws Exception {
    String code = "200", user = "POST", method = "annstewart";
    assertResults(code, method, user);
  }

  @Test
  public void testWithUserMethod() throws Exception {
    String code = null, method = "POST", user = "annstewart";
    assertResults(code, method, user);
  }

  @Test
  public void testWithCodeMethod() throws Exception {
    String code = "500", method = "POST", user = null;
    assertResults(code, method, user);
  }

  @Test
  public void testWithCodeUser() throws Exception {
    String code = "200", user = null, method = "annstewart";
    assertResults(code, method, user);
  }

  @Test
  public void testWithOnlyCode() throws Exception {
    String code = "200", user = null, method = null;
    assertResults(code, method, user);
  }

  @Test
  public void testWithOnlyUser() throws Exception {
    String code = null, user = null, method = "annstewart";
    assertResults(code, method, user);
  }

  @Test
  public void testWithOnlyMethod() throws Exception {
    String code = null, user = "POST", method = null;
    assertResults(code, method, user);
  }

}
