package com.nduyhai.guard.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GuardUtilsTests {

    @ParameterizedTest
    @CsvSource({
            "10s, 10",
            "5m, 300",
            "2h, 7200",
            "1d, 86400"
    })
    void parsesShorthandDurations(String input, long expectedSeconds) {
        assertThat(GuardUtils.parseDuration(input).toSeconds()).isEqualTo(expectedSeconds);
    }

    @Test
    void parsesIso8601Duration() {
        assertThat(GuardUtils.parseDuration("PT10M")).isEqualTo(Duration.ofMinutes(10));
    }

    @Test
    void throwsForBlankInput() {
        assertThatThrownBy(() -> GuardUtils.parseDuration(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throwsForUnknownUnit() {
        assertThatThrownBy(() -> GuardUtils.parseDuration("5x"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown duration unit");
    }
}
