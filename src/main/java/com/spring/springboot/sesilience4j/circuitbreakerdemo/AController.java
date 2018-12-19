package com.spring.springboot.sesilience4j.circuitbreakerdemo;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @Description: TODO
 * @Auther: birenjie
 * @Date: 2018-12-18 17:33
 */
public class AController {
    private static final Logger logger = LoggerFactory.getLogger(AController.class);

    CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
            .ringBufferSizeInClosedState(2)
            .ringBufferSizeInHalfOpenState(2)
            .failureRateThreshold(100)
            .waitDurationInOpenState(Duration.ofMillis(1000))
            .build();

    CircuitBreaker circuitBreaker = CircuitBreaker.of("requestTest",circuitBreakerConfig);
    AService aService = new AService();
    ExecutorService executorService = Executors.newCachedThreadPool();

    @Test
    public void requestTest(){
        int threadCount = 10;

        CircuitBreakerUtil.addCircuitBreakerListener(circuitBreaker);

        for(int i=0;i< threadCount;i++){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                }
                request();
        }
    }


    private void request(){
        logger.info("request  start ......");

        CircuitBreakerUtil.getCircuitBreakerStatus(circuitBreaker);


        // 模拟失败调用，并链接降级函数
        CheckedFunction0<Boolean> checkedSupplier = CircuitBreaker.decorateCheckedSupplier(circuitBreaker, () -> aService.checkUser());
        Try<Boolean> result = Try.of(checkedSupplier)
                .recover(throwable -> {
                    CircuitBreakerUtil.getCircuitBreakerStatus(circuitBreaker);

                    //此处两种情况:1.熔断器开启导致服务被降级, 2.服务调用异常导致服务被降级
                    logger.info("request AService 被降级啦  ......");
                    return true;
                });

        Boolean serviceResult =  result.get();

        logger.info("request  end ......");

        logger.info("===========================================================================================");
    }


}
