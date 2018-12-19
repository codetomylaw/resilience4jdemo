package org.spring.springboot.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.Map;

/**
 * Spring Boot HelloWorld案例
 *
 * Created by bysocket on 16/4/26.
 */
@Controller
public class HelloWorldController {
    private static final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);

    @RequestMapping("/")
    public String sayHello(Map<String,Object> map) {
        map.put("name", "jack");
        logger.info("Current Time: {}", System.currentTimeMillis());

        return "/templates/welcome";

    }
}
