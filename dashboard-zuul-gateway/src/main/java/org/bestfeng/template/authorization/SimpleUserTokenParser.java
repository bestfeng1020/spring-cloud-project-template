package org.bestfeng.template.authorization;

import org.hswebframework.web.authorization.basic.web.ParsedToken;
import org.hswebframework.web.authorization.basic.web.UserTokenParser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author bestfeng
 * @since 1.0
 */
@Component
public class SimpleUserTokenParser implements UserTokenParser {
    @Override
    public ParsedToken parseToken(HttpServletRequest request) {
        String header = request.getHeader("template-token");
        if (StringUtils.isEmpty(header)) {
            return null;
        }
        return new ParsedToken() {
            @Override
            public String getToken() {
                return header;
            }

            @Override
            public String getType() {
                return "template-token";
            }
        };
    }
}
