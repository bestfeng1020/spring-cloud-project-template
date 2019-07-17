package org.bestfeng.template;

import com.netflix.zuul.filters.FilterRegistry;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;


/**
 * @author bestfeng
 * @since 1.0
 */
@SpringCloudApplication
@EnableZuulProxy
@EnableHystrix
@EnableFeignClients
public class DashboardGateWayApplication {
    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(DashboardGateWayApplication.class, args);
        FilterRegistry.instance().remove("sendErrorFilter");
    }

}
