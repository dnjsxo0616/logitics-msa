package com.iftrue.gateway.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iftrue.gateway.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import org.springframework.web.server.ServerWebExchange;

@Component
@RequiredArgsConstructor
public class ResponseWriter {

    private final ObjectMapper objectMapper;

    public Mono<Void> write(
            ServerWebExchange exchange,
            ErrorCode errorCode
    ) {
        ApiResponse response =
                ApiResponse.fail(errorCode);

        exchange.getResponse().setStatusCode(errorCode.getStatus());
        exchange.getResponse()
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(response);

            return exchange.getResponse().writeWith(
                    Mono.just(
                            exchange.getResponse()
                                    .bufferFactory()
                                    .wrap(bytes)
                    )
            );
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}
