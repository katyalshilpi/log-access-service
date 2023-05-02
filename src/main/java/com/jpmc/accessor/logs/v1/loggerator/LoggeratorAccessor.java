package com.jpmc.accessor.logs.v1.loggerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.jpmc.accessor.logs.v1.model.LogEntry;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Setter
@Getter
public class LoggeratorAccessor {

  // Regex for log entry fields
  // IP Address ^(\d.+)  rfc931 (\w+)  DateTime (\[\d+\/\w+\/\d+ \d+\:\d+\:\d+ [+-]\d{4}\])   Request ("(.+?)")  Status (\d{3})   Bytes (\d+)
  private static final String LOG_ENTRY_PATTERN = "^(\\d.+) (\\S+) (\\S+) (\\[\\d+\\/\\w+\\/\\d+ \\d+\\:\\d+\\:\\d+ [+-]\\d{4}\\]) (\".+?\") (\\d{3}) (\\d+)";
  private static final Pattern PATTERN = Pattern.compile(LOG_ENTRY_PATTERN);

  @Value("${loggerator.host}")
  private String host;

  @Value("${loggerator.port}")
  private int port;

  public Set<LogEntry> getLogs(String code, String method, String user) throws Exception {
    long start = System.currentTimeMillis();
    // TODO This list could become a memory bottleneck for huge data, we need to decide how to manage this more efficiently
    Set<LogEntry> logList = new TreeSet<>();

    // wait for a client to connect
    try (Socket clientSocket = new Socket(host, port)) {
      log.info("Client connected from - " + clientSocket.getInetAddress() + " to loggerator running on port - " + port);

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
        // Append the log line to the logBuilder
        String line;
        while ((line = reader.readLine()) != null) {
          Matcher matcher = PATTERN.matcher(line);
          if (matcher.matches()) {
            String remoteHost = matcher.group(1);
            String rfc931 = matcher.group(2);
            String authUser = matcher.group(3);
            String dateStr = matcher.group(4);
            String request = matcher.group(5);
            String status = matcher.group(6);
            String bytes = matcher.group(7);

            if (containsCode(code, status) && containsUser(user, authUser) && containsMethod(method, request)) {
              LogEntry logEntry = new LogEntry(remoteHost, rfc931, authUser, dateStr, request, status, bytes);
              logList.add(logEntry);
            }
          }
          else {
            log.warn("Invalid log entry format - matcher could not match with the log entry - " + line);
          }

        }
      }
      catch (IOException e) {
        log.error("Exception thrown while reading from loggerator - " + e.getMessage());
        throw e;
      }
    }
    catch (IOException e) {
      log.error("IO Exception thrown while establishing socket connection to loggerator - " + e.getMessage());
      throw e;
    }
    // TODO Convert to metric
    long end = System.currentTimeMillis();
    log.info("Total time taken to fetch filtered logs: " + (end - start) / 1000 + "sec");
    return logList;
  }

  private boolean containsCode(String code, String logStatus) {
    return Strings.isNullOrEmpty(code) || code.equals(logStatus);
  }

  private boolean containsUser(String user, String logAuthUser) {
    return Strings.isNullOrEmpty(user) || user.equals(logAuthUser);
  }

  private boolean containsMethod(String method, String logRequest) {
    return Strings.isNullOrEmpty(method) || (logRequest != null && logRequest.startsWith("\"" + method));
  }

}
