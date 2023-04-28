package com.jpmc.accessor.logs.v1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.ServletException;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.assertj.core.util.VisibleForTesting;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * Smoke test REST APIs
 * <p>
 * To run this test, run command mvn -Dtest=com.jpmc.accessor.ogs.SmokeIT test
 * <p>
 * Springboot service needs to be running for this smoke test java -Xmx500m -jar target/log-access-service.jar
 */
public class SmokeIT {

  /**
   * Set to {@code true} to throw errors instead of returning responses.
   */
  protected boolean raiseHttpClientErrors = false;

  private Integer port = 18080;
  private String host = "localhost";

  /**
   * Used to access endpoints during testing. Adjusts URIs for host and port so that they can be specified by path.
   */
  protected RestTemplate restTemplate;

  public SmokeIT() {
    MockitoAnnotations.initMocks(this);
    restTemplate = new RestTemplate(new AdjustedClientHttpRequestFactory());
    restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
      @Override
      public boolean hasError(ClientHttpResponse response) throws IOException {
        // return response instead of throwing HttpClientException in rest template
        return raiseHttpClientErrors && restTemplate.getErrorHandler().hasError(response);
      }
    });
  }

  /**
   * Host to connect to defaults to "localhost".
   */
  protected String host() {
    return host;
  }

  /**
   * Port to connect
   */
  protected int port() {
    return port;
  }

  /**
   * Test /ping endpoint.
   */
  @Test
  public void testPing() throws IOException, ServletException {
    ResponseEntity<String> response = this.restTemplate.getForEntity("/ping", String.class);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isEqualToIgnoringCase("ping successful");
  }

  /**
   * Test /version endpoint.
   */
  @Test
  public void testVersion() throws IOException, ServletException {
    ResponseEntity<String> response = this.restTemplate.getForEntity("/version", String.class);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

    String projectVersion;

    try {
      projectVersion = JsonPath.read(response.getBody(), "$.projectVersion");
    }
    catch (PathNotFoundException e) {
      fail("Unable to find projectVersion in JSON message");
      return;
    }

    assertThat(projectVersion).isNotEmpty();
    assertThat(projectVersion).doesNotContain("@");
  }

  /**
   * Test /logs endpoint.
   */
  @Test
  public void testGetLogs() throws IOException {
    ResponseEntity response = this.restTemplate.getForEntity("/logs?code=403&method=PUT&user=minus_aut", Object.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }

  /**
   * Generate client HTTP requests converted for the appropriate host and port. Host and port are defined by methods on the parent class.
   */
  @VisibleForTesting
  class AdjustedClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
      try {
        return super.createRequest(adjustUri(uri), httpMethod);
      }
      catch (URISyntaxException e) {
        throw new IllegalStateException("Error rebuilding URI", e);
      }
    }

    @VisibleForTesting
    URI adjustUri(URI uri) throws URISyntaxException {
      return new URI("http", uri.getUserInfo(), host(), port(), uri.getPath(), uri.getQuery(), uri.getFragment());
    }
  }
}
