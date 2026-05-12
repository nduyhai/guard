package com.nduyhai.guard.samples;

import com.nduyhai.guard.annotation.EnableGuard;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Demo Spring Boot application showcasing Guard annotations.
 *
 * <p>{@code @EnableGuard} is optional here because {@code guard-spring-boot-starter} activates
 * Guard via auto-configuration. It is included for clarity.
 */
@EnableGuard
@SpringBootApplication
public class SampleApplication {

  static void main(String[] args) {
    SpringApplication.run(SampleApplication.class, args);
  }
}
