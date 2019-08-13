package org.bestfeng.template.redis.provider;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import org.bestfeng.template.logging.AccessLoggerRequest;
import org.bestfeng.template.logging.AccessLoggerResponse;
import org.bestfeng.template.logging.SystemLoggerInfo;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

/**
 * @author zhouhao
 * @since 1.0
 */
@AllArgsConstructor
public class RedisLoggingProvider {

    private RedissonClient redissonClient;

    @EventListener
    @Async
    public void handleAccessLoggerRequest(AccessLoggerRequest request) {
        redissonClient
                .getTopic("template-access-logger-request", new StringCodec())
                .publish(JSON.toJSONString(request));
    }

    @EventListener
    @Async
    public void handleAccessLoggerResponse(AccessLoggerResponse response) {
        redissonClient
                .getTopic("template-access-logger-response", new StringCodec())
                .publish(JSON.toJSONString(response));
    }

    @EventListener
    @Async
    public void handleSystemLogger(SystemLoggerInfo info) {
        redissonClient
                .getTopic("template-system-logger", new StringCodec())
                .publish(JSON.toJSONString(info));
    }
}
