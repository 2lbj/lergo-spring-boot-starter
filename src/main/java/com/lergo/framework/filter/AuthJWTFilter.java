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
@ConditionalOnProperty(value = "lergo.filter.authJWT", havingValue = "true")
@Order(999)
public class AuthJWTFilter extends BaseFilter implements WebFilter {

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Value("${lergo.filter.auth-header-name:Authorization}")
    private String authHeaderName;
    @Value("${lergo.filter.auth-expire-seconds:120}")
    private int authExpireSeconds;

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

            String testAppKey = "testAppkey";
            String testAppSecret = "*************************";

            String authHeader = req.getHeaders().getFirst("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                JwtTool.JwtVerifyResult jwtVerifyResult = JwtTool.claimsToken(token, testAppKey, testAppSecret);

                if(jwtVerifyResult.getSuccess()){
                    Map<String, String> payload = jwtVerifyResult.getPayload();

                    log.info("负载： {} \r\n ts-{} iat-{} exp-{}",
                            payload,
                            DateUtil.date(Long.parseLong(payload.get("ts"))),
                            DateUtil.date(Long.parseLong(payload.get("iat")+"000")),
                            DateUtil.date(Long.parseLong(payload.get("exp")+"000"))
                    );

                    // 刷新token过期时间
                    //res.getHeaders().set("Authorization", "Bearer "+JwtTool.createToken(payload, testAppKey, testAppSecret, Collections.singletonList("role-admin")));

                    return chain.filter(exchange);
                }else{
                    log.error("验证失败："+jwtVerifyResult.getMsg());
                }
            }

//            MultiValueMap<String, String> params = req.getQueryParams();
//            HttpHeaders headers = req.getHeaders();
//
//            // 从header获取token
//            String token = headers.getFirst(authHeaderName);
//
//            // 从参数中获取token
//            if (StringUtils.isNotBlank(params.getFirst(authHeaderName))) {
//                token = params.getFirst(authHeaderName);
//            }
//
//            // 校验token是否有效
//            boolean valid = false;
//            if (token != null) {
//                valid = Boolean.TRUE.equals(stringRedisTemplate.hasKey(token));
//            }
//
//            // 校验通过，过滤器正常放行
//            if (valid) {
//                // 刷新token过期时间
//                stringRedisTemplate.expire(token, authExpireSeconds, TimeUnit.SECONDS);
//                return chain.filter(exchange);
//            }

            // 校验不通过，返回错误信息
            res.setStatusCode(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
            res.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            byte[] bytes = "NON_AUTHORITATIVE".getBytes();
            return res.writeWith(Mono.just(res.bufferFactory().wrap(bytes)));

        });
    }

}