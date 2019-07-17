package org.bestfeng.template.div;

import org.hswebframework.web.authorization.basic.configuration.EnableAopAuthorize;
import org.hswebframework.web.authorization.listener.event.AuthorizingHandleBeforeEvent;
import org.hswebframework.web.dao.Dao;
import org.hswebframework.web.dev.tools.EnableDevTools;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author bestfeng
 * @since 1.0
 */
@SpringCloudApplication
@EnableFeignClients
@MapperScan(basePackages = "org.bestfeng.template.dao", markerInterface = Dao.class)
@ComponentScan("org.bestfeng.template")
@EnableAopAuthorize
@Component
@EnableDevTools//开启或者关闭开发者服务
@EnableCaching
public class DevOpsServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DevOpsServerApplication.class, args);
    }

    @EventListener
    public void adminHasAllPermission(AuthorizingHandleBeforeEvent e) {
        if (e.getContext().getAuthentication().getUser().getUsername().equals("admin")) {
            e.setAllow(true);
        }
    }
}
