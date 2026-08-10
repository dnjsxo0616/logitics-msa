package com.iftrue.order.presentation.controller.api;

import com.iftrue.order.global.security.AuthenticatedUser;
import com.iftrue.order.presentation.dto.OrderCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Order", description = "주문 관리 API")
@RequestMapping("/api/v1/orders")
public interface OrderApi {

    @Operation(
            summary = "주문 생성",
            description = "수령 업체가 상품을 주문합니다."
    )
    @PostMapping
    ResponseEntity<Void> createOrder(@RequestBody @Valid OrderCreateRequest request,
                                     @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user);
}
