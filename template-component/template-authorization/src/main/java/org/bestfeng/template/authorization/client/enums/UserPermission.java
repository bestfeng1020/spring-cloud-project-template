package org.bestfeng.template.authorization.client.enums;

import com.alibaba.fastjson.annotation.JSONType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bestfeng.template.authorization.client.CustomerUserAuthentication;
import org.hswebframework.web.authorization.exception.AccessDenyException;
import org.hswebframework.web.dict.Dict;
import org.hswebframework.web.dict.EnumDict;

/**
 * @author wangzheng
 * @see
 * @since 1.0
 */
@Getter
@AllArgsConstructor
@JSONType(deserializer = EnumDict.EnumDictJSONDeserializer.class)
@Dict(id = "user-permission")
public enum UserPermission implements EnumDict<String> {

    INVITE_HOME_MEMBER("invite-home-member", "邀请家庭成员", true),
    BIND_DEVICE_GATEWAY("bind-device-gateway", "绑定网关", true),
    UNBIND_DEVICE_GATEWAY("unbind-device-gateway", "解绑当前网关", true),
    CONTROL_ACCESS_DEVICE("control-access-device", "拉黑/恢复网关下挂的上网设备", true),
    UNBIND_DEVICE_NETWORKING("unbind-device-networking", "解绑当前网关", true),
    BUY_GATEWAY_PLUGIN("buy-gateway-plugin", "插件订购", true),
    INSTALL_GATEWAY_PLUGIN("install-gateway-plugin", "插件安装", true),
    UNINSTALL_GATEWAY_PLUGIN("uninstall-gateway-plugin", "插件卸载", true),
    UPDATE_GATEWAY_PLUGIN("update-gateway-plugin", "插件升级", true);

    private String text;

    private String permissionName;

    private boolean selfUpdatable;

    @Override
    public String getValue() {
        return name();
    }

    public void assertPermission(String gatewayIdOrSn, CustomerUserAuthentication authentication) {
        if (!authentication.hasPermission(gatewayIdOrSn, getValue())) {
            throw new AccessDenyException();
        }
    }
}
