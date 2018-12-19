package com.spring.springboot.sesilience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;

import java.time.Duration;

/**
 * @Description: TODO
 * @Auther: birenjie
 * @Date: 2018-11-29 10:07
 */
public class Resilience4jDemo {

//    CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();
    // 创建定制化熔断器配置
    CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofMillis(1000))
            .ringBufferSizeInHalfOpenState(2)
            .ringBufferSizeInClosedState(2)
            .build();

    // 使用定制化配置创建熔断器注册中心
    CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);

    // 从注册中心获取使用默认配置的熔断器
    CircuitBreaker circuitBreaker2 = circuitBreakerRegistry.circuitBreaker("otherName");

    // 从注册中心获取使用定制化配置的熔断器
    CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("uniqueName", circuitBreakerConfig);


//    选择不经过注册中心，直接创建熔断器实例
    CircuitBreaker defaultCircuitBreaker = CircuitBreaker.ofDefaults("testName");

    CircuitBreaker customCircuitBreaker = CircuitBreaker.of("testName", circuitBreakerConfig);



//    函数链只有在熔断器处于关闭或半开状态时才可以被调用

    // 创建熔断器
//    CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("testName");

    // 用熔断器包装函数
    CheckedFunction0<String> decoratedSupplier = CircuitBreaker
            .decorateCheckedSupplier(circuitBreaker, () -> "This can be any method which returns: 'Hello");

    // 链接其它的函数
    Try<String> result = Try.of(decoratedSupplier)
            .map(value -> value + " world'");

    // 如果函数链中的所有函数均调用成功，最终结果为Success<String>
//    assertThat(result.isSuccess()).isTrue();
//    assertThat(result.get()).isEqualTo("This can be any method which returns: 'Hello world'");



}
