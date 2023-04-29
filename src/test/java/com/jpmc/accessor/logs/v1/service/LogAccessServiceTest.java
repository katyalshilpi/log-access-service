package com.jpmc.accessor.logs.v1.service;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.jpmc.accessor.logs.v1.loggerator.LoggeratorAccessor;
import com.jpmc.accessor.logs.v1.model.LogEntry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class LogAccessServiceTest {

  @Mock
  private LoggeratorAccessor mockLogerratorAccessor;

  private LogAccessService logAccessService;

  private final LogEntry logEntry1 = new LogEntry("23.59.50.157", "-", "annstewart", "[18/Jul/2000 02:12:31 +0000]", "\"POST /photos/90 HTTP/1.0\"", "200", "97");
  private final LogEntry logEntry2 = new LogEntry("23.59.50.157", "-", "annstewart", "[18/Jul/2000 02:12:31 +0000]", "\"POST /photos/90 HTTP/1.0\"", "500", "97");
  private final Set<LogEntry> logResponse = new TreeSet<>();

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    logAccessService = new LogAccessService(mockLogerratorAccessor);
    logResponse.add(logEntry1);
    logResponse.add(logEntry2);
  }

  public void assertResult(String code, String method, String user) throws Exception {
    String result1 = logAccessService.getLogs(code, method, user).iterator().next().toString();
    assertEquals("Log result does not match", logEntry1.toString(), result1);
  }

  public void assertEmptyResult(String code, String method, String user) throws Exception {
    Set<LogEntry> result1 = logAccessService.getLogs(code, method, user);
    assertTrue("Empty list not returned for multiple values", (result1 != null && result1.size() == 0));
  }

  @Test
  public void testWithNone() throws Exception {
    String code = null, user = null, method = null;
    when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(logResponse);
    assertResult(code, method, user);
  }

  @Test
  public void testWithCodeUserMethod() throws Exception {
    String code = "200", method = "POST", user = "annstewart";
    when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(logResponse);
    assertResult(code, method, user);
  }

  @Test
  public void testWithUserMethod() throws Exception {
    String code = null, method = "POST", user = "annstewart";
    when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(logResponse);
    assertResult(code, method, user);
  }

  @Test
  public void testWithCodeMethod() throws Exception {
    String code = "500", method = "POST", user = null;
    Set<LogEntry> list = new TreeSet<>();
    list.add(logEntry2);
    when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(list);
    String result1 = logAccessService.getLogs(code, method, user).iterator().next().toString();
    assertEquals("Log result does not match", logEntry2.toString(), result1);
  }

  @Test
  public void testWithCodeUser() throws Exception {
    String code = "200", method = null, user = "annstewart";
    List<LogEntry> list = new LinkedList<>();
    when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(logResponse);
    assertResult(code, method, user);
  }

  @Test
  public void testWithOnlyCode() throws Exception {
    String code = "200", method = null, user = null;
    when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(logResponse);
    assertResult(code, method, user);
  }

  @Test
  public void testWithCommaSeparatedCode() throws Exception {
    String code = "200,400", method = null, user = null;
    when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(new TreeSet<>());
    assertEmptyResult(code, method, user);
  }

  @Test
  public void testWithOnlyUser() throws Exception {
    String code = null, method = null, user = "annstewart";
    when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(logResponse);
    assertResult(code, method, user);
  }

  @Test
  public void testWithCommaSeparatedUser() throws Exception {
    String code = null, method = null, user = "annstewart,aut";
    when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(new TreeSet<>());
    assertEmptyResult(code, method, user);
  }

  @Test
  public void testWithSpaceInUser() throws Exception {
    String code = null, method = null, user = "ann stewart";
    when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(new TreeSet<>());
    assertEmptyResult(code, method, user);
  }

  @Test
  public void testWithOnlyMethod() throws Exception {
    String code = null, method = "POST", user = null;
    when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(logResponse);
    assertResult(code, method, user);
  }

  @Test
  public void testWithCommaSeparatedMethod() throws Exception {
    String code = null, method = "POST,GET", user = null;
    when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(new TreeSet<>());
    assertEmptyResult(code, method, user);
  }

  @Test(expected = IOException.class)
  public void testException() throws Exception {
    String code = null, method = "POST", user = null;
    when(mockLogerratorAccessor.getLogs(code, method, user)).thenThrow(IOException.class);
    logAccessService.getLogs(code, method, user);
  }

}
