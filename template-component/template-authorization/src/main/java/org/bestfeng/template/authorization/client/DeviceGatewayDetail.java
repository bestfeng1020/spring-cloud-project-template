package org.bestfeng.template.authorization.client;

import lombok.Data;

import java.util.List;

/**
 * @author wangzheng
 * @see
 * @since 1.0
 */
@Data
public class DeviceGatewayDetail {

    private String id;

    private String serialNo;

    private List<String> permissions;

    private List<String> roles;

    // TODO: 18-9-17 tuozhangegnduoshuxing
}
