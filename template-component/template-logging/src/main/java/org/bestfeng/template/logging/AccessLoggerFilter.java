package org.bestfeng.template.logging;

import org.hswebframework.web.logging.AccessLoggerInfo;

/**
 * @author zhouhao
 * @since 1.1.0
 */
public interface AccessLoggerFilter {
    /**
     * 是否过滤此日志,过滤后不再记录日志
     * @param request
     * @return
     */
    boolean match(AccessLoggerInfo request);
}
