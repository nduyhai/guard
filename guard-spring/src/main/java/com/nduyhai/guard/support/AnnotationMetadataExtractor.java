package com.nduyhai.guard.support;

import com.nduyhai.guard.annotation.AuditLog;
import com.nduyhai.guard.annotation.DistributedLock;
import com.nduyhai.guard.annotation.Idempotent;
import com.nduyhai.guard.annotation.RateLimit;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * Extracts Guard annotations from a method and its declaring class, with results
 * cached by {@link MethodClassKey} to avoid repeated reflection.
 *
 * <p>Method-level annotations take precedence over class-level annotations.
 */
public final class AnnotationMetadataExtractor {

    private static final List<Class<? extends Annotation>> GUARD_ANNOTATIONS = List.of(
            Idempotent.class, DistributedLock.class, RateLimit.class, AuditLog.class);

    private final ConcurrentHashMap<MethodClassKey, Map<Class<? extends Annotation>, Annotation>> cache =
            new ConcurrentHashMap<>();

    /**
     * Returns a map of Guard annotation type → annotation instance for the given method.
     * Returns an empty map if the method carries no Guard annotations.
     */
    public Map<Class<? extends Annotation>, Annotation> extract(Method method, Class<?> targetClass) {
        MethodClassKey key = new MethodClassKey(method, targetClass);
        return cache.computeIfAbsent(key, k -> doExtract(method, targetClass));
    }

    private Map<Class<? extends Annotation>, Annotation> doExtract(Method method, Class<?> targetClass) {
        Map<Class<? extends Annotation>, Annotation> result = new HashMap<>();
        for (Class<? extends Annotation> type : GUARD_ANNOTATIONS) {
            // Method-level wins over class-level
            Annotation annotation = AnnotationUtils.findAnnotation(method, type);
            if (annotation == null) {
                annotation = AnnotationUtils.findAnnotation(targetClass, type);
            }
            if (annotation != null) {
                result.put(type, annotation);
            }
        }
        return Map.copyOf(result);
    }
}
