package com.jpmc.accessor.logs.v1.controller;

import java.util.List;
import javax.ws.rs.Produces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.accessor.logs.v1.model.JPMCLog;
import com.jpmc.accessor.logs.v1.service.LogAccessService;
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
//
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiResponse;
//import io.swagger.annotations.ApiResponses;
//import lombok.extern.slf4j.Slf4j;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

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
//  @ApiOperation(value = "Returns scrabble words",
//      notes = "Returns possible Scrabble words for a given set of letters. <br>" +
//          "The list will be sorted by the word score in descending order <br>" +
//          "If no dictionary words can be spelled with the given letters, then empty list is returned. <br><br>" +
//          "Status code 400 will be returned if the letters are not alphabetic" )
//  @ApiResponses(value = {
//      @ApiResponse(code = 200, message = "Success"),
//      @ApiResponse(code = 400, message = "Client error, must be letters"),
//      @ApiResponse(code = 401, message = "Not used"),
//      @ApiResponse(code = 403, message = "Not used"),
//      @ApiResponse(code = 404, message = "Not used"),
//      @ApiResponse(code = 500, message = "Server error")
//  })
  public ResponseEntity<StreamingResponseBody> streamLogs(@RequestParam(name = "code", required = false) String code,
                                                       @RequestParam(name = "method", required = false) String method,
                                                       @RequestParam(name = "user", required = false) String user) throws Exception {
    List<JPMCLog> resultList = logAccessService.getLogs(code, method, user);
    StreamingResponseBody responseBody = response -> {
      for (JPMCLog jpmcLog: resultList) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(jpmcLog) +"\n";
        response.write(jsonString.getBytes());
        response.flush();
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          log.error("Error streaming logs from loggerator - " + e.getMessage());
        }
      }
    };

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(responseBody);
  }
}
