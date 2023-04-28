package com.jpmc.accessor.logs.v1.loggerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.jpmc.accessor.logs.v1.model.JPMCLog;
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

  @VisibleForTesting
  @Value("${loggerator.host}")
  String host;

  @VisibleForTesting
  @Value("${loggerator.count}")
  int count;

  @Value("${loggerator.port}")
  int port;

  @Value("${loggerator.seed}")
  int seed;

  public List<JPMCLog> getLogs(String code, String method, String user) throws IOException {
    // TODO This list could become a memory bottleneck for huge data, we need to decide how to manage this more efficiently
    List<JPMCLog> logList = new LinkedList<>();

    // wait for a client to connect
    try (Socket clientSocket = new Socket(host, port)) {
      log.info("Client connected from " + clientSocket.getInetAddress() + " to loggerator running on port" + port);

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
        // Append the log line to the logBuilder
        String line;
        while ((line = reader.readLine()) != null) {
          String[] logParts = line.split(" ");
          if (logParts.length == 11) {
            // TODO Parse the log entry to get the log fields based on - https://www.w3.org/Daemon/User/Config/Logging.html
            // final String regex = "^(\\S+) (\\S+) (\\S+) " +
            //        "\\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(\\S+)" +
            //        " (\\S+)\\s*(\\S+)?\\s*\" (\\d{3}) (\\S+)";
            String dateString = logParts[3] + ' ' + logParts[4] + ' ' + logParts[5];
            String request = logParts[6] + ' ' + logParts[7] + ' ' + logParts[8];

            JPMCLog jpmcLog = new JPMCLog(logParts[0], logParts[1], logParts[2], dateString, request, logParts[9], Integer.parseInt(logParts[10]));

            if (codeExists(code, jpmcLog.getStatus()) && userExists(user, jpmcLog.getAuthUser()) && methodExists(method, jpmcLog.getRequest())) {
              logList.add(jpmcLog);
            }
          }
        }
      }
      catch (IOException e) {
        log.error("IO Exception thrown while reading from loggerator - " + e.getMessage());
        throw e;
      }
    }
    catch (IOException e) {
      log.error("IO Exception thrown while establishing socket connection to loggerator - " + e.getMessage());
      throw e;
    }
    return logList;
  }

  private boolean codeExists(String code, String logStatus) {
    return Strings.isNullOrEmpty(code) || logStatus.equals(code);
  }

  private boolean userExists(String user, String logAuthUser) {
    return Strings.isNullOrEmpty(user) || logAuthUser.equals(user);
  }

  private boolean methodExists(String method, String logRequest) {
    return Strings.isNullOrEmpty(method) || logRequest.toUpperCase().startsWith("\"" + method.toUpperCase());
  }

}
