package org.bestfeng.template.logging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zhouhao
 * @since 1.0
 */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class AccessLoggerResponse {
    private String id;

    private long responseTime;

    private long useTime;

    private Object response;

    private String exception;
}
