package com.jpmc.accessor.logs.v1.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.Produces;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.jpmc.accessor.logs.v1.model.LogEntry;
import com.jpmc.accessor.logs.v1.service.LogAccessService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping
@Produces("application/json")
@ResponseBody
@Slf4j
public class LogAccessController {

  private LogAccessService logAccessService;

  @Autowired
  public LogAccessController(LogAccessService logAccessService) {
    this.logAccessService = logAccessService;
  }

  @GetMapping("/logs")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value = "Streams logs from loggerator", notes = "Establishing socket connection with the loggerator and streams the logs back. <br>" +
      "The list will be sorted by the date in descending order <br>" + "If no log entries are returned by the logerator then empty list is returned. " +
      "The current implementation assumes that there will be single value passed in the request parameters and it will not support comma-separated " +
      "multiple parameter values. If comma-separated values are passed then it will return an empty list. <br><br>")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 401, message = "Not used"),
                         @ApiResponse(code = 403, message = "Not used"), @ApiResponse(code = 404, message = "Not used"),
                         @ApiResponse(code = 500, message = "Server error")})
  public ResponseEntity<StreamingResponseBody> streamLogs(
      @ApiParam(value = "Http Response Code", example = "200, 500") @RequestParam(name = "code", required = false) final String code,
      @ApiParam(value = "Http Request Method", example = "GET, POST, PUT") @RequestParam(name = "method", required = false) final String method,
      @ApiParam(value = "Username with which the user has authenticated himself", example = "testUser") @RequestParam(name = "user", required = false) final String user)
      throws Exception {
    Set<LogEntry> resultList = logAccessService.getLogs(code, method, user);
    List<String> stringList = resultList.stream().map(LogEntry::toString).collect(Collectors.toList());

    StreamingResponseBody responseBody = response -> {
      try {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(stringList);
        response.write(json.getBytes());
        response.flush();
      }
      catch (Exception e) {
        log.error("Error writing streaming response body - " + e.getMessage());
      }
    };

    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseBody);
  }
}
