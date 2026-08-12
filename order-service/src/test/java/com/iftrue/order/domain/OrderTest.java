package com.iftrue.order.domain;

import com.iftrue.order.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class OrderTest {

    @Test
    @DisplayName("주문을 생성하면 PENDING 상태가 된다")
    void createOrder() {
        Order order = createSampleOrder();
        assertThat(order.getQuantity()).isEqualTo(10);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("수량이 0이면 주문을 생성할 수 없다")
    void cannotCreateOrderWithZeroQuantity() {
        assertThatThrownBy(() -> Order.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                0,
                "요청사항",
                futureArrivalAt()
        )).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("수량이 음수이면 주문을 생성할 수 없다")
    void cannotCreateOrderWithNegativeQuantity() {
        assertThatThrownBy(() -> Order.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                -1,
                "요청사항",
                futureArrivalAt()
        )).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("PENDING 주문의 요청사항을 수정할 수 있다")
    void canUpdateRequestMessageWhenPending() {
        Order order = createSampleOrder();

        order.updateRequestMessage("변경된 요청사항");

        assertThat(order.getRequestMessage())
                .isEqualTo("변경된 요청사항");
    }

    @Test
    @DisplayName("PENDING이 아니면 요청사항을 수정할 수 없다")
    void cannotUpdateRequestMessageWhenNotPending() {
        Order order = createSampleOrder();
        order.confirm();

        assertThatThrownBy(() ->
                order.updateRequestMessage("변경된 요청사항")
        ).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("PENDING 주문을 확정할 수 있다")
    void canConfirmPendingOrder() {
        Order order = createSampleOrder();

        order.confirm();

        assertThat(order.getStatus())
                .isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("PENDING 주문을 실패 처리할 수 있다")
    void canFailPendingOrder() {
        Order order = createSampleOrder();

        order.fail();

        assertThat(order.getStatus())
                .isEqualTo(OrderStatus.FAILED);
    }

    @Test
    @DisplayName("CONFIRMED 주문을 취소할 수 있다")
    void canCancelConfirmedOrder() {
        Order order = createSampleOrder();
        order.confirm();

        order.cancel();

        assertThat(order.getStatus())
                .isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("CONFIRMED 주문을 완료할 수 있다")
    void canCompleteConfirmedOrder() {
        Order order = createSampleOrder();
        order.confirm();

        order.complete();

        assertThat(order.getStatus())
                .isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("허용되지 않는 상태로 변경할 수 없다")
    void cannotChangeToInvalidStatus() {
        Order order = createSampleOrder();

        assertThatThrownBy(order::complete)
                .isInstanceOf(BusinessException.class);

        assertThat(order.getStatus())
                .isEqualTo(OrderStatus.PENDING);
    }

    private Order createSampleOrder() {
        return Order.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                10,
                "요청사항",
                futureArrivalAt()
        );
    }

    private Instant futureArrivalAt() {
        return Instant.now().plusSeconds(86_400);
    }
}
