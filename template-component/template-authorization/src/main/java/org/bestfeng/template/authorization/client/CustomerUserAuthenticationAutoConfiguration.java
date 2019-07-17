package org.bestfeng.template.authorization.client;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangzheng
 * @see
 * @since 1.0
 */
@Configuration
public class CustomerUserAuthenticationAutoConfiguration {

    @Bean
    public CustomerAuthorizationArgumentResolver customerAuthorizationArgumentResolver(){
        return new CustomerAuthorizationArgumentResolver();
    }

    @Bean
    public BeanPostProcessor customerUserAuthenticationInitProcess() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof CustomerUserAuthenticationSupplier) {
                    CustomerUserAuthenticationHolder.registerSupplier(((CustomerUserAuthenticationSupplier) bean));
                }
                return bean;
            }
        };
    }
}
