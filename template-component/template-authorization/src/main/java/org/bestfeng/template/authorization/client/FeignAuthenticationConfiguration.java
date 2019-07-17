package org.bestfeng.template.authorization.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import feign.RequestInterceptor;
import org.apache.commons.codec.binary.Base64;
import org.hswebframework.web.ThreadLocalUtils;
import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.organizational.authorization.PersonnelAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouhao
 * @since 1.0
 */
@Configuration
public class FeignAuthenticationConfiguration {
    @Bean
    public RequestInterceptor microServiceAuthRequestInterceptor() {

        return template -> {
            String requestId = ThreadLocalUtils.get("request-id", () -> IDGenerator.SNOW_FLAKE.generate().toString());
            Authentication authentication = Authentication.current().orElse(null);
            PersonnelAuthentication personnelAuthentication = PersonnelAuthentication.current().orElse(null);
            CustomerUserAuthentication customerUserAuthentication = CustomerUserAuthentication.current().orElse(null);
            JSONObject autzInfo = new JSONObject();
            autzInfo.put("user", authentication);
            autzInfo.put("personnel", personnelAuthentication);
            autzInfo.put("customer", customerUserAuthentication);
            template.header("request-id", requestId);
            if (null != authentication) {
                template.header("iot-autz", Base64.encodeBase64String(JSON.toJSONBytes(autzInfo)));
            }
        };
    }

    @Bean
    public IotMicroServiceTokenParser iotMicroServiceTokenParser() {
        return new IotMicroServiceTokenParser();
    }
}
