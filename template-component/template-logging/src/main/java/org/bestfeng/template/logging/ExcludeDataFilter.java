package org.bestfeng.template.logging;

import lombok.Getter;
import lombok.Setter;
import org.hswebframework.web.logging.AccessLoggerInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author zhouhao
 * @since 1.1.0
 */
@Component
@ConfigurationProperties(prefix = "access.logger.data.exclude")
public class ExcludeDataFilter implements AccessLoggerFilter {

    @Getter
    @Setter
    private Set<String> headers = new HashSet<>(Arrays.asList("iot-autz", "iot-user", "iot-basic","Authentication"));

    @Getter
    @Setter
    private Set<String> parameters = new HashSet<>(Arrays.asList("password","authentication"));

    @Override
    public boolean match(AccessLoggerInfo request) {
        Map<String, String> headers = request.getHttpHeaders();
        if (headers != null) {
            this.headers.forEach(headers::remove);
        }
        Map<String, Object> parameters = request.getParameters();

        if (parameters != null) {
            this.parameters.forEach(parameters::remove);
        }

        return false;
    }
}
