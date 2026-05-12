package com.nduyhai.guard.support;

import java.time.Duration;
import org.springframework.boot.convert.DurationStyle;

public final class GuardUtils {

  private GuardUtils() {}

  public static Duration parseDuration(String value) {
    return DurationStyle.detectAndParse(value);
  }
}
