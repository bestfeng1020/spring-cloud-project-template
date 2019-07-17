package org.bestfeng.template.logging;

import lombok.*;

/**
 * @author zhouhao
 * @since 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemLoggerInfo {
    private long id;

    private String requestId;

    private String appId;

    private String appName;

    private String mavenModule;

    private String name;

    private String threadName;

    private String level;

    private String className;

    private String commitId;

    private String methodName;

    private int lineNumber;

    private String message;

    private String exceptionStack;

    private Long createTime;

    private String threadId;
}
