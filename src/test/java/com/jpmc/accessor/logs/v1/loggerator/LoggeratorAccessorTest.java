package com.jpmc.accessor.logs.v1.loggerator;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import java.util.Set;
import java.util.TreeSet;

import com.jpmc.accessor.logs.v1.model.LogEntry;
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
  }

  public void assertResults(String code, String method, String user) throws Exception {
    Set<LogEntry> logList = loggeratorAccessor.getLogs(code, method, user);
    assertEquals("Result not returned.", (logList != null), true);
    if (logList != null && logList.size() > 0) {
      LogEntry log = logList.iterator().next();
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

  public void assertEmptyResult(String code, String method, String user) throws Exception {
    Set<LogEntry> result1 = loggeratorAccessor.getLogs(code, method, user);
    assertTrue("Empty list not returned for multiple values", (result1 != null && result1.size() == 0));
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
  public void testWithCommaSeparatedCode() throws Exception {
    String code = "200,400", method = null, user = null;
    assertEmptyResult(code, method, user);
  }

  @Test
  public void testWithOnlyUser() throws Exception {
    String code = null, user = null, method = "annstewart";
    assertResults(code, method, user);
  }

  @Test
  public void testWithCommaSeparatedUser() throws Exception {
    String code = null, method = null, user = "annstewart,aut";
    assertEmptyResult(code, method, user);
  }

  @Test
  public void testWithSpaceInUser() throws Exception {
    String code = null, method = null, user = "ann stewart";
    assertEmptyResult(code, method, user);
  }

  @Test
  public void testWithOnlyMethod() throws Exception {
    String code = null, user = "POST", method = null;
    assertResults(code, method, user);
  }

  @Test
  public void testWithCommaSeparatedMethod() throws Exception {
    String code = null, method = "POST,GET", user = null;
    assertEmptyResult(code, method, user);
  }

}
