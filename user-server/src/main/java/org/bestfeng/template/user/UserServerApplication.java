package org.bestfeng.template.user;

import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.authorization.basic.configuration.EnableAopAuthorize;
import org.hswebframework.web.dao.Dao;
import org.hswebframework.web.loggin.aop.EnableAccessLogger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author bestfeng
 * @since 1.0
 */
@SpringCloudApplication
@Configuration
@EnableAopAuthorize
@EnableCaching
@ComponentScan("org.bestfeng.template")
@EnableAccessLogger
@EnableFeignClients
@MapperScan(basePackages = "org.bestfeng.template.user.dao", markerInterface = Dao.class)
@Slf4j(topic = "system.user-server.startup")
public class UserServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServerApplication.class, args);
        log.info("用户服务启动完成");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> log.info("用户服务停止")));
    }
}
