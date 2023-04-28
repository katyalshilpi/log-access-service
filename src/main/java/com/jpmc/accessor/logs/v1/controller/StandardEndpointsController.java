package com.jpmc.accessor.logs.v1.controller;

import javax.annotation.PostConstruct;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serve standard microservice endpoints, such as:
 *
 * <ul>
 *     <li>{@code /health}</li>
 *     <li>{@code /ping}</li>
 *     <li>{@code /version}</li>
 * </ul>
 * 
 * {@code /health} or {@code /ping} is used by load balancer for health check
 * {@code /version} is to check the current version of the service
 */
@RestController
@Slf4j
public class StandardEndpointsController {

    @VisibleForTesting static final String PING_SUCCESSFUL = "ping successful";

    private String version = "VERSION UNAVAILABLE. CHECK LOG FOR REASON.";

    @PostConstruct
    public void init() {
        log.info("Initializing...");

        try {
            version = getResource("version.json");
        } catch(Exception e) {
            log.warn("Cannot read version.json from classpath.", e);
        }

        log.info("Initialized");
    }

    @RequestMapping(value="/ping", method=RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "ping service",
    notes = "Return pong to show that the service is running. <br>" +
            "Returns HTTP status code 200 and pong if service is running.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Not used"),
            @ApiResponse(code = 403, message = "Not used"),
            @ApiResponse(code = 404, message = "Not used"),
            @ApiResponse(code = 503, message = "Service not available")
    })
    public String ping() {
        return PING_SUCCESSFUL;
    }

    @RequestMapping(value="/version", method= RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "version of service",
    notes = "Return version, build number and build time of the running service")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Not used"),
            @ApiResponse(code = 403, message = "Not used"),
            @ApiResponse(code = 404, message = "Not used"),
            @ApiResponse(code = 503, message = "Service not available")
    })
    public String getVersion() {
        return version;
    }

    private String getResource(String fileName) {
        String result = null;

        try {
            result = Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
        } catch(Exception e) {
            log.info("Resource {} not found.", fileName);
        }

        return result;
    }
}
