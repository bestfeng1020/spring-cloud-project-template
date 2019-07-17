package org.bestfeng.template.authorization;

import feign.RequestInterceptor;
import org.apache.commons.codec.binary.Base64;
import org.hswebframework.web.ThreadLocalUtils;
import org.hswebframework.web.id.IDGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bestfeng
 * @since 1.0
 */
@Configuration
public class MicroServiceAuthConfiguration {

    @Value("${user-server.client-id:admin}")
    private String clientId = "admin";

    @Value("${user-server.client-security:admin}")
    private String clientSecurity = "admin";

    @Bean
    public RequestInterceptor microServiceAuthRequestInterceptor() {

        return template -> {
            String requestId = ThreadLocalUtils.get("request-id", () -> IDGenerator.SNOW_FLAKE.generate().toString());
            template.header("request-id", requestId);
            template.header("Authorization", "iot-basic " + Base64.encodeBase64String(String.join(":", clientId, clientSecurity)
                    .getBytes()));
        };
    }


}
