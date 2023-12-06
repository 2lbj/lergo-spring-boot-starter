package com.lergo.framework.filter;

import cn.hutool.core.date.DateUtil;
import com.lergo.framework.annotation.UnAuthentication;
import com.lergo.framework.utils.JwtTool;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnProperty(value = "lergo.filter.auth-jwt", havingValue = "true")
@Order(999)
public class AuthJWTFilter extends BaseFilter implements WebFilter {

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Value("${lergo.filter.auth-header-name:Authorization}")
    private String authHeaderName;
    @Value("${lergo.filter.auth-expire-seconds:3600}")
    private int authExpireSeconds;

    @Value("${lergo.jwt.app-key:lerGo}")
    private String jwtKey;
    @Value("${lergo.jwt.app-secret:io.github.2lbj}")
    private String jwtSecret;
    @Value("${lergo.jwt.leeway-seconds:120}")
    private int jwtLeewaySeconds;
    @Value("${lergo.jwt.refresh:false}")
    private boolean jwtRefresh;

    @NotNull
    public Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {

        ServerHttpRequest req = exchange.getRequest();
        ServerHttpResponse res = exchange.getResponse();

        if (writeList(req)) {
            return chain.filter(exchange);
        }

        // 获取请求对应的HandlerMethod
        Mono<HandlerMethod> handlerMethodMono = requestMappingHandlerMapping
                .getHandler(exchange).cast(HandlerMethod.class);

        return handlerMethodMono.flatMap(handlerMethod -> {

            // 判断Method是否含有对应注解
            if (handlerMethod.hasMethodAnnotation(UnAuthentication.class)) {
                // 免登录注解，正常放行
                return chain.filter(exchange);
            }

            String authHeader = req.getHeaders().getFirst(authHeaderName);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                JwtTool.JwtVerifyResult jwtVerifyResult = JwtTool.claimsToken(token,
                        jwtKey, jwtSecret, jwtLeewaySeconds);

                if(jwtVerifyResult.getSuccess()){
                    Map<String, String> payload = jwtVerifyResult.getPayload();

                    log.info("负载： {} iat {} exp {}",
                            payload,
                            DateUtil.date(Long.parseLong(payload.get("iat")+"000")),
                            DateUtil.date(Long.parseLong(payload.get("exp")+"000"))
                    );

                    // 刷新token过期时间
                    if (jwtRefresh) {
                        res.getHeaders().set(authHeaderName, "Refresh " +
                                JwtTool.createToken(jwtKey, jwtSecret, authExpireSeconds, payload));
                        log.debug("payload {} refresh expire time", payload);
                    }

                    return chain.filter(exchange);
                }else{
                    log.error("验证失败："+jwtVerifyResult.getMsg());
                }
            }

            // 校验不通过，返回错误信息
            res.setStatusCode(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
            res.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            byte[] bytes = "NON_AUTHORITATIVE".getBytes();
            return res.writeWith(Mono.just(res.bufferFactory().wrap(bytes)));

        });
    }

}