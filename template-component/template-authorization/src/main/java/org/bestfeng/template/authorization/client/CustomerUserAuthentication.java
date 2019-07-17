package org.bestfeng.template.authorization.client;

import org.hswebframework.web.authorization.exception.AccessDenyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wangzheng
 * @see
 * @since 1.0
 */
public interface CustomerUserAuthentication {

    static Optional<CustomerUserAuthentication> current() {
        return Optional.ofNullable(CustomerUserAuthenticationHolder.getCurrent());
    }

    String getId();

    String getName();

    String getUsername();

    String getPhone();

    boolean isAdministrator();

    // TODO: 18-7-25 更多属性

    Set<String> accessGatewayIds();

    Set<String> accessSerialNos();

    List<DeviceGatewayDetail> accessGateways();

    default boolean hasPermission(String gatewayIdOrSn, String permission) {
        List<DeviceGatewayDetail> validateList = accessGateways().stream()
                .filter(deviceGatewayDetail -> deviceGatewayDetail.getId().equals(gatewayIdOrSn)
                        || deviceGatewayDetail.getSerialNo().equals(gatewayIdOrSn))
                .collect(Collectors.toList());
        if (validateList.size() > 0) {
            return validateList
                    .stream()
                    .allMatch(deviceGatewayDetail -> deviceGatewayDetail.getPermissions().contains(permission));
        } else {
            return false;
        }
    }

    default boolean hasRole(String gatewayId, String role) {
        List<DeviceGatewayDetail> validateList = accessGateways().stream()
                .filter(deviceGatewayDetail -> deviceGatewayDetail.getId().equals(gatewayId))
                .collect(Collectors.toList());
        if (validateList.size() > 0) {
            return validateList
                    .stream()
                    .allMatch(deviceGatewayDetail -> deviceGatewayDetail.getRoles().contains(role));
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        boolean success = new ArrayList<String>()
                .stream()
                .allMatch(s -> s.equals("123"));
    }

    default void assertHasGatewayAccess(String gatewayId) {
        if (!accessGatewayIds().contains(gatewayId)) {
            throw new AccessDenyException("没有访问此网关的权限");
        }
    }

    default boolean hasGatewayAccess(String gateWayId) {
        if (gateWayId == null) {
            return false;
        }
        return accessGatewayIds().stream().anyMatch(gateWayId::equals);
    }

    default boolean hasGatewayAccessBySn(String sn) {
        if (sn == null) {
            return false;
        }
        return accessGateways()
                .stream()
                .map(DeviceGatewayDetail::getSerialNo)
                .anyMatch(sn::equals);
    }
}
