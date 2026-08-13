package com.iftrue.order.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O-001", "주문을 찾을 수 없습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "O-002", "입력값 검증에 실패했습니다."),
    INSUFFICIENT_ORDER_QUANTITY(HttpStatus.CONFLICT, "O-003", "주문 수량이 부족합니다."),
    INVALID_ORDER_STATUS(HttpStatus.CONFLICT, "O-004", "허용되지 않는 주문 상태입니다."),
    ORDER_MODIFICATION_NOT_ALLOWED(HttpStatus.CONFLICT, "O-005", "주문을 수정할 수 없습니다."),
    ORDER_CANCELLATION_NOT_ALLOWED(HttpStatus.CONFLICT, "O-006", "주문을 취소할 수 없습니다."),
    DUPLICATE_ORDER_REQUEST(HttpStatus.CONFLICT, "O-007", "중복된 주문 요청입니다."),
    EXTERNAL_SERVICE_CALL_FAILED(HttpStatus.BAD_GATEWAY, "O-008", "외부 서비스 호출에 실패했습니다."),
    UNAUTHENTICATED_REQUEST(HttpStatus.UNAUTHORIZED, "O-009", "인증되지 않은 요청입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "O-010", "요청에 대한 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "O-011", "서버 오류가 발생했습니다."),

    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "O-012", "업체를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "O-013", "상품을 찾을 수 없습니다."),
    RECIPIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "O-014", "수령 담당자를 찾을 수 없습니다."),
    PRODUCT_SUPPLIER_MISMATCH(HttpStatus.CONFLICT, "O-015", "상품의 공급 업체가 주문 정보와 일치하지 않습니다."),
    RECIPIENT_COMPANY_MISMATCH(HttpStatus.CONFLICT, "O-016", "수령 담당자의 소속 업체가 수령 업체와 일치하지 않습니다."),
    INSUFFICIENT_PRODUCT_STOCK(HttpStatus.CONFLICT, "O-017", "상품 재고가 부족합니다."),
    DELIVERY_CREATION_FAILED(HttpStatus.BAD_GATEWAY, "O-018", "배송 생성에 실패했습니다."),
    INVENTORY_RESTORE_FAILED(HttpStatus.BAD_GATEWAY, "O-019", "차감된 재고 복원에 실패했습니다."),
    RECIPIENT_REQUIRED(HttpStatus.BAD_REQUEST, "O-020", "MASTER 주문에는 수령 담당자가 필요합니다."),
    DELIVERY_CANCELLATION_FAILED(HttpStatus.BAD_GATEWAY, "O-021", "배송 취소에 실패했습니다."),
    INVALID_RECIPIENT_INFO(HttpStatus.BAD_GATEWAY, "O-022", "수령 담당자 정보가 올바르지 않습니다."),
    INVALID_PRODUCT_INFO(HttpStatus.BAD_GATEWAY, "O-023", "상품 정보가 올바르지 않습니다."),
    INVALID_REQUESTED_ARRIVAL_TIME(HttpStatus.BAD_REQUEST, "O-024", "희망 도착 시각이 올바르지 않습니다."),
    REQUESTED_ARRIVAL_OUTSIDE_WORKING_HOURS(HttpStatus.BAD_REQUEST, "O-025", "희망 도착 시각은 09:00부터 18:00 사이여야 합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
