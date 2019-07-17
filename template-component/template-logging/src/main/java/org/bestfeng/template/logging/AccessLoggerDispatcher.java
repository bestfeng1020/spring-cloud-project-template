package org.bestfeng.template.logging;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.utils.StringUtils;
import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.bean.FastBeanCopier;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.logging.events.AccessLoggerAfterEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 访问日志转发
 *
 * @author zhouhao
 * @since 1.0
 */
@Component
@Slf4j
public class AccessLoggerDispatcher {

    @Autowired
    public ApplicationContext context;

    @Value("${spring.application.name:unknown}")
    private String serviceId = "unknown";

    private static final Class excludes[] = {
            ServletRequest.class,
            ServletResponse.class,
            InputStream.class,
            OutputStream.class,
            MultipartFile.class,
            MultipartFile[].class
    };

    @Autowired
    private List<AccessLoggerFilter> filters;

    @EventListener
    public void handleLoggingAfter(AccessLoggerAfterEvent event) {
        //获取全局请求ID
        String requestId = event.getLogger()
                .getHttpHeaders()
                .getOrDefault("request-id", IDGenerator.SNOW_FLAKE_STRING.generate());

        boolean matched = filters.stream().anyMatch(filter -> filter.match(event.getLogger()));
        if (matched) {
            //skip publish logging
            return;
        }

        //转换日志信息
        AccessLoggerInfo info = new AccessLoggerInfo();
        Map<String, Object> logMap = event.getLogger().toSimpleMap(obj -> {
            if (Stream.of(excludes).anyMatch(aClass -> aClass.isInstance(obj))) {
                return obj.getClass().getName();
            }
            return (Serializable) JSON.toJSON(obj);
        });
        FastBeanCopier.copy(logMap, info, "id");

        Authentication.current().ifPresent(auth -> {
            info.setUserId(auth.getUser().getId());
            info.setUserName(auth.getUser().getName());
        });

        info.setId(IDGenerator.SNOW_FLAKE_STRING.generate());
        info.setRequestId(requestId);
        info.setLogId(event.getLogger().getId());
        info.setServiceId(serviceId);
        info.setClassName(event.getLogger().getTarget().getName());
        info.setMethodName(event.getLogger().getMethod().getName());
        info.setServiceHost(context.getEnvironment()
                .getProperty("eureka.instance.hostname", System.getProperty("HOSTNAME")));

        //设置响应日志
        //info.setResponse(JSON.toJSONString(event.getLogger().getResponse()));
        info.setUseTime(event.getLogger().getResponseTime() - event.getLogger().getRequestTime());
        info.setException(event.getLogger().getException() == null ? ""
                : StringUtils.throwable2String(event.getLogger().getException()));
        context.publishEvent(info);
    }

}
