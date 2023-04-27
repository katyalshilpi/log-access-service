package com.jpmc.accessor.logs.v1.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@EqualsAndHashCode
public class JPMCLog {

  // Remote hostname (or IP number if DNS hostname is not available, or if DNSLookup is Off
  private String remoteHost;
  // The remote logname of the user
  private String rfc931;
  // The username as which the user has authenticated himself
  private String authUser;
  // Date and time of the request.
  private String dateStr;
  // The request line exactly as it came from the client
  private String request;
  // The HTTP status code returned to the client
  private String status;
  // The content-length of the document transferred
  private int bytes;

  public JPMCLog(String remoteHost, String rfc931, String authUser, String dateStr,
                 String request, String status, int bytes) {
    this.remoteHost = remoteHost;
    this.rfc931 = rfc931;
    this.authUser = authUser;
    this.dateStr = dateStr;
    this.request = request;
    this.status = status;
    this.bytes = bytes;
  }

  public String toString() {
    return remoteHost + ' ' + rfc931 + ' ' + authUser + ' ' + dateStr + ' ' + request + ' ' + status + ' ' + bytes;
  }

  public Date getDate() {
    try {
      // TODO Improve code to remove the Square brackets
      String tempDate = dateStr.replace("[", "");
      tempDate = tempDate.replace("]", "");
      SimpleDateFormat dt = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss zzz");
      return dt.parse(tempDate);
    } catch(Exception e) {
      log.error("Could not parse date due to error - " + e.getMessage());
    }
    return null;
  }

}
