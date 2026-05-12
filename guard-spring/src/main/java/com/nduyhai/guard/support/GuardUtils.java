package com.nduyhai.guard.support;

import java.time.Duration;

/** Shared parsing utilities for the Guard framework. */
public final class GuardUtils {

  private GuardUtils() {}

  /**
   * Parses a duration string in Guard shorthand ({@code Ns}, {@code Nm}, {@code Nh}, {@code Nd}) or
   * ISO-8601 format ({@code PTnHnMnS}).
   *
   * @throws IllegalArgumentException for unrecognised formats or units
   */
  public static Duration parseDuration(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Duration value must not be blank");
    }
    String trimmed = value.trim();
    if (trimmed.charAt(0) == 'P' || trimmed.charAt(0) == 'p') {
      return Duration.parse(trimmed);
    }
    char unit = trimmed.charAt(trimmed.length() - 1);
    long amount;
    try {
      amount = Long.parseLong(trimmed.substring(0, trimmed.length() - 1));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid duration format: '" + value + "'", e);
    }
    return switch (unit) {
      case 's', 'S' -> Duration.ofSeconds(amount);
      case 'm', 'M' -> Duration.ofMinutes(amount);
      case 'h', 'H' -> Duration.ofHours(amount);
      case 'd', 'D' -> Duration.ofDays(amount);
      default ->
          throw new IllegalArgumentException(
              "Unknown duration unit '" + unit + "' in '" + value + "'. Use s/m/h/d or ISO-8601.");
    };
  }
}
