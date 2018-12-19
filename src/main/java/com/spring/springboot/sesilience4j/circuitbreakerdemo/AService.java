package com.spring.springboot.sesilience4j.circuitbreakerdemo;

import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: TODO
 * @Auther: birenjie
 * @Date: 2018-12-18 17:16
 */
public class AService {
    private static final Logger logger = LoggerFactory.getLogger(AService.class);

    public Boolean checkUser() throws Exception{
        //模拟服务异常
        if ( RandomUtils.nextInt(2) == 1){
            throw new RuntimeException("ERROR");
        }
        //模拟服务调用时间
        Thread.sleep(200);

        logger.info("request AService 服务正常  ......");
        return true;
    }
}
