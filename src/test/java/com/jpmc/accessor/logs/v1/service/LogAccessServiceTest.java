package com.jpmc.accessor.logs.v1.service;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.jpmc.accessor.logs.v1.loggerator.LoggeratorAccessor;
import com.jpmc.accessor.logs.v1.model.JPMCLog;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class LogAccessServiceTest {

    @Mock
    private LoggeratorAccessor mockLogerratorAccessor;
    
    private LogAccessService logAccessService;

    private final JPMCLog jpmcLog1 = new JPMCLog("23.59.50.157", "-", "annstewart", "[18/Jul/2000 02:12:31 +0000]",
                    "\"POST /photos/90 HTTP/1.0\"", "200", 97);
    private final JPMCLog jpmcLog2 = new JPMCLog("23.59.50.157", "-", "annstewart", "[18/Jul/2000 02:12:31 +0000]",
                                                "\"POST /photos/90 HTTP/1.0\"", "500", 97);
    private final List<JPMCLog> logResponse = new LinkedList<>();

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        logAccessService = new LogAccessService(mockLogerratorAccessor);
        logResponse.add(jpmcLog1);
        logResponse.add(jpmcLog2);
    }

    @Test
    public void testWithNone() throws Exception {
        String code = null, user = null, method = null;
        when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(logResponse);
        String result1 = logAccessService.getLogs(code, method, user).get(0).toString();
        assertEquals("Log result does not match", jpmcLog1.toString(), result1);
    }

    @Test
    public void testWithCodeUserMethod() throws Exception  {
        String code = "200", user = "POST", method = "annstewart";
        when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(logResponse);
        String result1 = logAccessService.getLogs(code, method, user).get(0).toString();
        assertEquals("Log result does not match", jpmcLog1.toString(), result1);
    }

    @Test
    public void testWithUserMethod() throws Exception  {
        String code = null, method = "POST", user = "annstewart";
        when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(logResponse);
        String result1 = logAccessService.getLogs(code, method, user).get(0).toString();
        assertEquals("Log result does not match", jpmcLog1.toString(), result1);

        String result2 = logAccessService.getLogs(code, method, user).get(1).toString();
        assertEquals("Log result does not match", jpmcLog2.toString(), result2);
    }

    @Test
    public void testWithCodeMethod() throws Exception {
        String code = "500", method = "POST", user = null;
        List<JPMCLog> list = new LinkedList<>();
        list.add(jpmcLog2);
        when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(list);
        String result1 = logAccessService.getLogs(code, method, user).get(0).toString();
        assertEquals("Log result does not match", jpmcLog2.toString(), result1);
    }

    @Test
    public void testWithCodeUser() throws Exception {
        String code = "200", user = null, method = "annstewart";
        List<JPMCLog> list = new LinkedList<>();
        when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(logResponse);
        String result1 = logAccessService.getLogs(code, method, user).get(0).toString();
        assertEquals("Log result does not match", jpmcLog1.toString(), result1);
    }

    @Test
    public void testWithOnlyCode() throws Exception {
        String code = "200", user = null, method = null;
        when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(logResponse);
        String result1 = logAccessService.getLogs(code, method, user).get(0).toString();
        assertEquals("Log result does not match", jpmcLog1.toString(), result1);
    }

    @Test
    public void testWithOnlyUser() throws Exception {
        String code = null, user = null, method = "annstewart";
        when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(logResponse);
        String result1 = logAccessService.getLogs(code, method, user).get(0).toString();
        assertEquals("Log result does not match", jpmcLog1.toString(), result1);
    }

    @Test
    public void testWithOnlyMethod() throws Exception {
        String code = null, user = "POST", method = null;

        when(mockLogerratorAccessor.getLogs(code, method, user)).thenReturn(logResponse);
        String result1 = logAccessService.getLogs(code, method, user).get(0).toString();
        assertEquals("Log result does not match", jpmcLog1.toString(), result1);
    }

    @Test(expected = IOException.class)
    public void testException() throws Exception {
        String code = null, user = "POST", method = null;
        when(mockLogerratorAccessor.getLogs(code, method, user)).thenThrow(IOException.class);
        logAccessService.getLogs(code, method, user);
    }

}
