package com.qing.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * 请求进入网关会碰到三类过滤器：当前路由的过滤器、DefaultFilter、GlobalFilter
 * 请求路由后，会将当前路由过滤器和DefaultFilter、GlobalFilter，合并到一个过滤器链（集合）中，排序后依次执行每个过滤器
 *
 * 每一个过滤器都必须指定一个int类型的order值，order值越小，优先级越高，执行顺序越靠前。
 * 当过滤器的order值一样时，会按照 defaultFilter > 路由过滤器 > GlobalFilter的顺序执行。
 */
@Order(-1)
//@Component
public class AuthorizeFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1 获取请求参数
        MultiValueMap<String, String> params = exchange.getRequest().getQueryParams();
        // 2 获取Authorization参数
        String auth = params.getFirst("authorization");
        // 3 校验
        if ("admin".equals(auth)) {
            // 放行
            return chain.filter(exchange);
        }

        // 4 拦截
        // 4.1 禁止访问
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);

        // 4.2 结束处理
        return exchange.getResponse().setComplete();
    }
}
