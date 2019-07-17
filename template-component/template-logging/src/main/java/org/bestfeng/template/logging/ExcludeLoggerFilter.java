package org.bestfeng.template.logging;

import lombok.Getter;
import lombok.Setter;
import org.hswebframework.web.logging.AccessLoggerInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zhouhao
 * @since 1.1.0
 */
@Component
@ConfigurationProperties("access.logger.excludes")
public class ExcludeLoggerFilter implements AccessLoggerFilter {

    private AntPathMatcher urlMatcher = new AntPathMatcher("/");

    private AntPathMatcher classMatcher = new AntPathMatcher(".");

    @Getter
    @Setter
    private Set<String> urls = new HashSet<>();

    @Getter
    @Setter
    private Set<String> java = new HashSet<>();

    @Override
    public boolean match(AccessLoggerInfo request) {

        if (!urls.isEmpty()) {
            boolean urlAnyMatch = urls.stream().anyMatch(url -> urlMatcher.match(url, request.getUrl()));
            if (urlAnyMatch) {
                return true;
            }
        }

        if (!java.isEmpty()) {
            String javaTarget = request.getTarget().getName().concat(".").concat(request.getMethod().getName());
            boolean javaAnyMatch = java.stream()
                    .anyMatch(pattern -> classMatcher
                            .match(pattern, javaTarget));
            if (javaAnyMatch) {
                return true;
            }
        }

        return false;
    }
}
