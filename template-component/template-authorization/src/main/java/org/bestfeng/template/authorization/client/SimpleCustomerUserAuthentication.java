package org.bestfeng.template.authorization.client;

import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class SimpleCustomerUserAuthentication implements CustomerUserAuthentication {

    private String id;

    private String name;

    private String phone;

    private String username;

    private boolean administrator;

    Set<String> accessGatewayIds;

    Set<String> accessSerialNos;

    List<DeviceGatewayDetail> accessGateways;

    @Override
    public Set<String> accessGatewayIds() {
        if (accessGatewayIds == null) {
            return accessGatewayIds = new HashSet<>();
        }
        return accessGatewayIds;
    }

    @Override
    public Set<String> accessSerialNos() {
        if (accessSerialNos==null){
            return accessSerialNos = new HashSet<>();
        }
        return accessSerialNos;
    }

    @Override
    public List<DeviceGatewayDetail> accessGateways() {
        if (accessGateways == null) {
            return accessGateways = new ArrayList<>();
        }
        return accessGateways;
    }
}
