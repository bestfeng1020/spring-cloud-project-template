package org.bestfeng.template.logging;

import lombok.*;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wangzheng
 * @see
 * @since 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessLoggerInfo implements Serializable {
    private String id;

    private String requestId;

    private String serviceId;

    private String serviceHost;

    private String logId;

    private String ip;

    private String url;

    private String className;

    private String methodName;

    private String httpMethod;

    private String action;

    private String describe;

    private long requestTime;

    private Map<String, String> parameters;

    private Map<String, String> httpHeaders;

    private String userName;

    private String userId;


    private long responseTime;

    private long useTime;

    //private String response;

    private String exception;
}
