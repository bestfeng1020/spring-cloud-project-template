package org.bestfeng.template.authorization.client.enums;

import com.alibaba.fastjson.annotation.JSONType;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
@Dict(id = "permission-type")
public enum PermissionType implements EnumDict<String> {
    // TODO: 19-3-1 xiu gai ming zi
    permission("ziyuan"),role("jiaose")
    ;

    private String text;

    @Override
    public String getValue() {
        return name();
    }
}
