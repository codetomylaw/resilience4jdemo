package org.spring.springboot.web;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;



/**
 * Spring Boot应用启动类
 *
 * Created by bysocket on 16/4/26.
 */
@ServletComponentScan
@SpringBootApplication
@ImportResource({"classpath:application-b.xml"})
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        // 程序启动入口
        // 启动嵌入式的Tomcat并初始化Spring环境及其各Spring组件
        SpringApplication.run(Application.class,args);
    }

}
