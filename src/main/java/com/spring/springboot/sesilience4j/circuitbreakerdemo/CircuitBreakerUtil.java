package com.spring.springboot.sesilience4j.circuitbreakerdemo;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: TODO
 * @Auther: birenjie
 * @Date: 2018-12-18 17:45
 */
public class CircuitBreakerUtil {
    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerUtil.class);

    /**
     * @Description: 获取熔断器的状态
     */
    public static void getCircuitBreakerStatus(CircuitBreaker circuitBreaker){
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        // Returns the failure rate in percentage.
        float failureRate = metrics.getFailureRate();
        // Returns the current number of buffered calls.
        int bufferedCalls = metrics.getNumberOfBufferedCalls();
        // Returns the current number of failed calls.
        int failedCalls = metrics.getNumberOfFailedCalls();
        // Returns the current number of successed calls.
        int successCalls = metrics.getNumberOfSuccessfulCalls();
        // Returns the max number of buffered calls.
        int maxBufferCalls = metrics.getMaxNumberOfBufferedCalls();
        // Returns the current number of not permitted calls.
        long notPermittedCalls = metrics.getNumberOfNotPermittedCalls();

        logger.info("state=" +circuitBreaker.getState() + " , metrics[ failureRate=" + failureRate +
                ", bufferedCalls=" + bufferedCalls +
                ", failedCalls=" + failedCalls +
                ", successCalls=" + successCalls +
                ", maxBufferCalls=" + maxBufferCalls +
                ", notPermittedCalls=" + notPermittedCalls +
                " ]"
        );
    }


    /**
     * @Description: 监听熔断器事件
     */
    public static void addCircuitBreakerListener(CircuitBreaker circuitBreaker){
        circuitBreaker.getEventPublisher()
                .onSuccess(event -> {
                    System.out.println("===eventType===" + event.getEventType());
                })
                .onError(event -> {
                    System.out.println("===eventType===" + event.getEventType());

                })
                .onIgnoredError(event -> {
                    System.out.println("===eventType===" + event.getEventType());
                })
                .onReset(event -> {
                    System.out.println("===eventType===" + event.getEventType());
                })
                .onStateTransition(event -> {
                    System.out.println("===eventType===" + event.getEventType());
                })
                .onCallNotPermitted(event -> {
                    System.out.println(" !!!!!!!!!!!!!!系统告警!!!!!!!!!!!!!!!!!!!!!!!!!");
                })
        ;
    }


}
