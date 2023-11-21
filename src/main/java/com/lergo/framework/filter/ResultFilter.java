package com.lergo.framework.filter;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.Charset;

@Slf4j
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnProperty(value = "lergo.filter.result", havingValue = "true")
@Order(100)//优先级，数字越小，优先级越高
public class ResultFilter extends BaseFilter implements WebFilter {

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

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

//        return handlerMethodMono.flatMap(handlerMethod -> {
//
//            //TODO: mono or flux 区别
//
////            DataBuffer wrap = res.bufferFactory().wrap(
////                    "NON_AUTHORITATIVE_INFORMATION"
////                            .getBytes(StandardCharsets.UTF_8));
////            return res.writeWith(Flux.just(wrap));
//
//            res.setStatusCode(HttpStatus.OK);
//            res.getHeaders().set("Content-Type", "application/json;charset=UTF-8");
//
//            System.out.println(res);
//
//            byte[] bytes = "OK".getBytes();
//            return res.writeWith(Mono.just(res.bufferFactory().wrap(bytes)));
//        });
        //获取response的 返回数据
        ServerHttpRequest originalRequest = exchange.getRequest();
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();

        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                HttpHeaders headers = getHeaders();
                ContentDisposition contentDisposition = headers.getContentDisposition();
                // 为附件的响应头 不打印响应日志
                if (HttpStatus.OK.equals(getStatusCode()) && (body instanceof Flux || body instanceof Mono) && !contentDisposition.isAttachment()) {
                    Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                        DataBuffer join = dataBufferFactory.join(dataBuffers);
                        byte[] content = new byte[join.readableByteCount()];
                        join.read(content);
                        String responseData = new String(content, Charset.forName("UTF-8"));
                        log.info("***********************************响应信息**********************************");
                        log.info("响应内容:{}", responseData);
                        log.info("****************************************************************************\n");
                        DataBufferUtils.release(join);
                        //byte[] uppedContent = responseData.getBytes();
                        log.info(originalRequest.getURI().toString());
                        return bufferFactory.wrap(content);
                    }));
                } else if (!contentDisposition.isAttachment()) {
                    //保存日志
                    log.error(originalRequest.getURI().toString());
                    log.error("响应code异常:{}", getStatusCode());
                }
                return super.writeWith(body);
            }
        };
        return chain.filter(exchange.mutate().response(decoratedResponse).build());

    }

}