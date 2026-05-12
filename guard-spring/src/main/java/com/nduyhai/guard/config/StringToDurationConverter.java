package com.nduyhai.guard.config;

import com.nduyhai.guard.support.GuardUtils;
import java.time.Duration;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;

/**
 * Spring {@link Converter} that parses Guard shorthand duration strings ({@code Ns}/{@code
 * Nm}/{@code Nh}/{@code Nd}) and ISO-8601 into {@link Duration}.
 *
 * <p>Registered automatically by {@link GuardConfiguration} so that
 * {@code @ConfigurationProperties} fields of type {@link Duration} accept Guard notation.
 */
public final class StringToDurationConverter implements Converter<String, Duration> {

  @Override
  public Duration convert(@NonNull String source) {
    return GuardUtils.parseDuration(source);
  }
}
