package com.nduyhai.guard.config;

import com.nduyhai.guard.aop.GuardAdvisor;
import com.nduyhai.guard.aop.GuardExecutionChain;
import com.nduyhai.guard.aop.GuardHandler;
import com.nduyhai.guard.aop.GuardMethodInterceptor;
import com.nduyhai.guard.support.AnnotationMetadataExtractor;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ConversionServiceFactoryBean;

/**
 * Core Guard configuration — registers the AOP advisor and shared infrastructure beans.
 *
 * <p>Imported by {@link GuardConfigurationSelector} (via {@code @EnableGuard}) and also applied by
 * {@code GuardAutoConfiguration} in Spring Boot deployments.
 */
@Configuration(proxyBeanMethods = false)
@EnableAspectJAutoProxy
public class GuardConfiguration {

  @Bean
  public AnnotationMetadataExtractor guardAnnotationMetadataExtractor() {
    return new AnnotationMetadataExtractor();
  }

  @Bean
  public GuardExecutionChain guardExecutionChain(@Autowired List<GuardHandler> handlers) {
    return new GuardExecutionChain(handlers);
  }

  @Bean
  public GuardMethodInterceptor guardMethodInterceptor(
      GuardExecutionChain executionChain, AnnotationMetadataExtractor extractor) {
    return new GuardMethodInterceptor(executionChain, extractor);
  }

  @Bean
  public GuardAdvisor guardAdvisor(GuardMethodInterceptor interceptor) {
    return new GuardAdvisor(interceptor);
  }

  @Bean
  public ConversionServiceFactoryBean guardConversionService() {
    ConversionServiceFactoryBean factory = new ConversionServiceFactoryBean();
    factory.setConverters(Set.of(new StringToDurationConverter()));
    return factory;
  }
}
