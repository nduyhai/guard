package com.nduyhai.guard.core;

/**
 * Base unchecked exception for all Guard framework errors.
 */
public class GuardException extends RuntimeException {

    public GuardException(String message) {
        super(message);
    }

    public GuardException(String message, Throwable cause) {
        super(message, cause);
    }
}
