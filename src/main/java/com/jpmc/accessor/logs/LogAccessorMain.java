package com.jpmc.accessor.logs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.jpmc.accessor.logs"})
public class LogAccessorMain {

  public static void main(String[] args) {
    SpringApplication.run(LogAccessorMain.class, args);
  }
}
