package cn.gybyt.advice;

import cn.gybyt.annotation.NotHandleResponse;
import cn.gybyt.util.BaseException;
import cn.gybyt.util.BaseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.HandlerResultHandler;
import org.springframework.web.reactive.result.method.InvocableHandlerMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 返回拦截
 *
 * @program: utils
 * @classname: ControllerResponseAdvice
 * @author: codetiger
 * @create: 2022/7/20 19:19
 **/
@RestControllerAdvice
@ConditionalOnClass(InvocableHandlerMethod.class)
public class ControllerFluxResponseAdvice implements HandlerResultHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean supports(HandlerResult result) {
        return !result.getReturnType()
                      .isAssignableFrom(BaseResponse.class) || !result.getReturnType()
                                                                      .isAssignableFrom(
                                                                              Mono.class) || !result.getReturnTypeSource()
                                                                                                    .hasMethodAnnotation(
                                                                                                            NotHandleResponse.class);
    }

    @Override
    public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        try {
            return response.writeWith(Mono.just(response.bufferFactory()
                                                        .wrap(OBJECT_MAPPER.writeValueAsBytes(
                                                                BaseResponse.success(result.getReturnValue())))));
        } catch (JsonProcessingException e) {
            throw new BaseException("序列化失败");
        }
    }

}
