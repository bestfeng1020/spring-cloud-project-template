package org.bestfeng.template.authorization;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.hswebframework.web.ThreadLocalUtils;
import org.hswebframework.web.WebUtil;
import org.hswebframework.web.authorization.basic.web.ParsedToken;
import org.hswebframework.web.authorization.basic.web.UserTokenParser;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.hswebframework.web.id.IDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * 将权限信息分发到子服务中
 *
 * @author bestfeng
 * @since 1.0
 */
@Component
@Slf4j
public class AuthorizationFilter extends ZuulFilter {

    @Autowired(required = false)
    private List<UserTokenParser> userTokenParsers;

    @Autowired
    private UserAuthorizeInfoClient authorizeInfoClient;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @PostConstruct
    public void init() {
        if (userTokenParsers == null) {
            SimpleSessionIdUserTokenParser parser = new SimpleSessionIdUserTokenParser();
            userTokenParsers = new ArrayList<>();
            userTokenParsers.add(parser);
        }
    }

    @Override
    public Object run() {
        String requestId = IDGenerator.SNOW_FLAKE_STRING.generate();
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        context.addZuulRequestHeader("request-id", requestId);
        context.addZuulResponseHeader("request-id", requestId);
        context.addZuulResponseHeader("X-Forwarded-For", WebUtil.getIpAddr(request));

        ThreadLocalUtils.put("request-id", requestId);
        if (request.getRequestURI().startsWith("/api/user-server")) {
            return null;
        }
        try {
            userTokenParsers.stream()
                    .map(parser -> parser.parseToken(request)) //解析token
                    .filter(Objects::nonNull)
                    .map(ParsedToken::getToken)
                    .map(authorizeInfoClient::getDetail)
                    .findAny()
                    .map(ResponseMessage::getResult)
                    .ifPresent(detail -> context.addZuulRequestHeader("template-autz", Base64.encodeBase64String(detail.getBytes())));

        } catch (Exception e) {
            log.warn("获取权限信息时发生错误", e);
        }
        return null;
    }
}
