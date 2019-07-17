package org.bestfeng.template;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.ERROR_TYPE;

/**
 * @author bestfeng
 * @since 1.0
 */
@Component
@Slf4j
public class GlobalErrorFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return ERROR_TYPE;
    }

    @Override
    public int filterOrder() {
        return -1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.getThrowable() != null;
    }

    @Override
    public Object run() {

        try {
            RequestContext ctx = RequestContext.getCurrentContext();
            Throwable err = ctx.getThrowable();
            HttpServletRequest request = ctx.getRequest();

            request.setAttribute("javax.servlet.error.status_code", 500);

            log.warn("Error during filtering", err);

            request.setAttribute("javax.servlet.error.message", "很抱歉，当前网络错误，请稍后重试");

            RequestDispatcher dispatcher = request.getRequestDispatcher("/error");
            if (dispatcher != null) {
                if (!ctx.getResponse().isCommitted()) {
                    ctx.setResponseStatusCode(500);
                    dispatcher.forward(request, ctx.getResponse());
                }
            }
        } catch (Exception ex) {
            ReflectionUtils.rethrowRuntimeException(ex);
        }
        return null;
    }
}