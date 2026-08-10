package com.iftrue.gateway.exception;


import com.iftrue.gateway.response.ResponseWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
@Order(-1)
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ResponseWriter responseWriter;

    @Override
    public Mono<Void> handle(
            ServerWebExchange exchange,
            Throwable ex
    ) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        return responseWriter.write(
                exchange,
                ErrorCode.INTERNAL_SERVER_ERROR
        );
    }
}
