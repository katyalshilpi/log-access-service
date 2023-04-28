package com.jpmc.accessor.logs.v1.controller;

import java.util.List;
import javax.ws.rs.Produces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.accessor.logs.v1.model.JPMCLog;
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
      "multiple parameter values <br><br>")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 401, message = "Not used"),
                         @ApiResponse(code = 403, message = "Not used"), @ApiResponse(code = 404, message = "Not used"),
                         @ApiResponse(code = 500, message = "Server error")})
  public ResponseEntity<StreamingResponseBody> streamLogs(
      @ApiParam(value = "Http Response Code", example = "200, 500") @RequestParam(name = "code", required = false) String code,
      @ApiParam(value = "Http Request Method", example = "GET, POST, PUT") @RequestParam(name = "method", required = false) String method,
      @ApiParam(value = "Username with which the user has authenticated himself", example = "testUser") @RequestParam(name = "user", required = false) String user)
      throws Exception {
    List<JPMCLog> resultList = logAccessService.getLogs(code, method, user);
    StreamingResponseBody responseBody = response -> {
      for (JPMCLog jpmcLog : resultList) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(jpmcLog) + "\n";
        response.write(jsonString.getBytes());
        response.flush();
        try {
          Thread.sleep(10);
        }
        catch (InterruptedException e) {
          log.error("Error streaming logs from loggerator - " + e.getMessage());
        }
      }
    };

    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseBody);
  }
}
