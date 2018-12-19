package com.spring.springboot.sesilience4j;

import com.spring.springboot.sesilience4j.circuitbreakerdemo.CircuitBreakerUtil;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedFunction1;
import io.vavr.control.Try;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.ws.WebServiceException;
import java.time.Duration;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

/**
 * @Description: 熔断器Demo
 * @Auther: birenjie
 * @Date: 2018-12-17 16:51
 */
public class CircuitBreakerAPIDemo {

    @Test
    public void circuitBreakerDemoTest(){

        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("testName");

        // 熔断器采用装饰器模式
        CheckedFunction0<String> decoratedSupplier = CircuitBreaker
                .decorateCheckedSupplier(circuitBreaker, () -> "This can be any method which returns: 'Hello");


//        CircuitBreaker.decorateCheckedRunnable()
//        CircuitBreaker.decorateCheckedFunction()
//        然后使用Vavr的Try.of(…​) / Try.run(…​) 调用被装饰的函数


        // and chain an other function with map     函数链只有在熔断器处于关闭或半开状态时才可以被调用
        Try<String> result = Try.of(decoratedSupplier)
                .map(value -> value + " world1'");


        //如果函数链中的所有函数均调用成功，最终结果为Success<String>
        Assert.assertTrue(result.isSuccess());

        Assert.assertEquals("error","This can be any method which returns: 'Hello world'",result.get());

    }

    
    /**
     * @Description: 函数链中可以包含被不同熔断器包装的多个函数
     */
    @Test
    public void circuitBreakerDemo2Test(){

        // 两个熔断器
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("testName");
        CircuitBreaker anotherCircuitBreaker = CircuitBreaker.ofDefaults("anotherTestName");

        // 用两个熔断器分别包装Supplier 和 Function
        CheckedFunction0<String> decoratedSupplier = CircuitBreaker
                .decorateCheckedSupplier(circuitBreaker, () -> "Hello");

        CheckedFunction1<String, String> decoratedFunction = CircuitBreaker
                .decorateCheckedFunction(anotherCircuitBreaker, (input) -> input + " world");

        // 链接函数
        Try<String> result = Try.of(decoratedSupplier)
                .mapTry(decoratedFunction::apply);

        //  如果函数链中的所有函数均调用成功，最终结果为Success<String>
        Assert.assertTrue(result.isSuccess());

        Assert.assertEquals("Hello world",result.get());
    }


    /**
     * @Description: 模拟熔断器被触发的情况，在熔断器打开的状态，Try.of 返回 Failure<Throwable>，链接函数将不会被调用
     */
    @Test
    public void circuitBreakerDemo3Test(){

        // 创建一个环状缓冲区大小为2的熔断器
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                //熔断器在关闭状态时环状缓冲区的大小
                .ringBufferSizeInClosedState(2)
                //触发熔断的失败率阈值
                .failureRateThreshold(50)
                //熔断器从打开状态到半开状态的等待时间
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .build();
        CircuitBreaker circuitBreaker = CircuitBreaker.of("testName", circuitBreakerConfig);

        // 模拟一次失败调用
        circuitBreaker.onError(0, new RuntimeException());
        // 没有触发熔断，熔断器仍处于关闭状态
        Assert.assertEquals(CircuitBreaker.State.CLOSED,circuitBreaker.getState());
        // 模拟第二次失败调用
        circuitBreaker.onError(0, new RuntimeException());
        // 失败率达到二次，熔断器被触发
        Assert.assertEquals(CircuitBreaker.State.OPEN,circuitBreaker.getState());

        // 由于熔断器处于打开状态，调用失败
        Try<String> result = Try.of(CircuitBreaker.decorateCheckedSupplier(circuitBreaker, () -> "Hello"))
                .map(value -> value + " world");

        Assert.assertEquals("Hello world",result.get());

        // 熔断器支持重置，重置之后所有状态数据清空，恢复初始状态。
        circuitBreaker.reset();

        Assert.assertEquals(CircuitBreaker.State.CLOSED,circuitBreaker.getState());

    }



    /**
     * @Description: 容器器降级
     */
    @Test
    public void circuitBreakerDemo4Test(){
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("testName");
        // 模拟失败调用，并链接降级函数
        CheckedFunction0<String> checkedSupplier = CircuitBreaker.decorateCheckedSupplier(circuitBreaker, () -> {
            throw new RuntimeException("BAM!");
        });
        //当Try.of() 返回 Failure<Throwable>时,使用降级
        Try<String> result = Try.of(checkedSupplier)
                .recover(throwable -> "Hello Recovery");
        // 降级函数被调用，最终调用结果为成功
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("Hello Recovery",result.get());
    }


    /**
     * @Description: 熔断器失败判定
     */
    @Test
    public void circuitBreakerDemo5Test(){
        //忽略除WebServiceException外的所有异常
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .ringBufferSizeInClosedState(2)
                .ringBufferSizeInHalfOpenState(2)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .recordFailure(throwable -> Match(throwable).of(
                        Case($(instanceOf(WebServiceException.class)), true),
                        Case($(), false)))
                .build();

        CircuitBreaker circuitBreaker = CircuitBreaker.of("testName",circuitBreakerConfig);

        // 模拟失败调用
        circuitBreaker.onError(0, new RuntimeException());
        circuitBreaker.onError(0, new RuntimeException());

        // 模拟失败调用
//        circuitBreaker.onError(0, new WebServiceException());
//        circuitBreaker.onError(0, new WebServiceException());

        // 没有触发熔断，熔断器仍处于关闭状态
        Assert.assertEquals(CircuitBreaker.State.CLOSED,circuitBreaker.getState());

    }


    /**
     * @Description: 监听熔断器事件
     */
    @Test
    public void circuitBreakerDemo6Test(){

        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("testName");

        // 分类监听事件
        circuitBreaker.getEventPublisher()
                .onSuccess(event -> System.out.println("===success===" + event.getEventType()))
                .onError(event -> System.out.println("===error===" + event.getEventType()))
                .onIgnoredError(event -> System.out.println("===ignoreError===" + event.getEventType()))
                .onReset(event -> System.out.println("===reset===" + event.getEventType()))
                .onStateTransition(event -> System.out.println("===StateTransition===" + event.getEventType()));

        // 监听所有事件
        circuitBreaker.getEventPublisher()
                .onEvent(event -> System.out.println("===event===" + event));

        // 熔断器采用装饰器模式
        CheckedFunction0<String> decoratedSupplier = CircuitBreaker
                .decorateCheckedSupplier(circuitBreaker, () -> "This can be any method which returns: 'Hello");

        Try<String> result = Try.of(decoratedSupplier)
                .map(value -> value + " world1'");

        Assert.assertTrue(result.isSuccess());

        circuitBreaker.onError(0, new RuntimeException());

        CircuitBreakerUtil.getCircuitBreakerStatus(circuitBreaker);
    }


}
