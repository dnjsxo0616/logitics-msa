package com.iftrue.notification.presentation.request;

import com.iftrue.notification.domain.aialert.AiRequestPayload;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record NotificationCreateRequest(
        @NotNull(message = "배송 ID는 필수입니다.")
        UUID deliveryId,

        @NotNull(message = "주문 ID는 필수입니다.")
        UUID orderId,

        @NotNull(message = "주문 시각은 필수입니다.")
        Instant orderedAt,

        @NotNull(message = "희망 도착 시각은 필수입니다.")
        Instant requestedArrivalAt,

        @NotNull(message = "공급 업체 ID는 필수입니다.")
        UUID supplierCompanyId,

        @NotNull(message = "수령 업체 ID는 필수입니다.")
        UUID recipientCompanyId,

        @NotBlank(message = "요청자 이름은 필수입니다.")
        String requesterName,

        @NotBlank(message = "요청자 이메일은 필수입니다.")
        @Email(message = "요청자 이메일 형식이 올바르지 않습니다.")
        String requesterEmail,

        @NotBlank(message = "요청자 Slack ID는 필수입니다.")
        String requesterSlackId,

        @Valid
        @NotNull(message = "상품 정보는 필수입니다.")
        ProductInfo product,

        String requestMessage,

        @Valid
        @NotNull(message = "출발 허브 정보는 필수입니다.")
        HubInfo departureHub,

        @Valid
        @NotNull(message = "경유 허브 목록은 필수입니다.")
        List<@NotNull(message = "경유 허브 정보는 필수입니다.") TransitHubInfo> transitHubs,

        @NotBlank(message = "도착지 주소는 필수입니다.")
        String destinationAddress,

        @Valid
        @NotNull(message = "출발 허브 담당자 정보는 필수입니다.")
        ManagerInfo departureHubManager,

        @NotNull(message = "배송 생성 시각은 필수입니다.")
        Instant deliveryCreatedAt,

        @NotNull(message = "총 예상 소요시간은 필수입니다.")
        @PositiveOrZero(message = "총 예상 소요시간은 0 이상이어야 합니다.")
        Integer totalExpectedDurationMinutes
) {

    public AiRequestPayload toAiRequestPayload() {
        return new AiRequestPayload(
                orderedAt,
                requestedArrivalAt,
                supplierCompanyId,
                recipientCompanyId,
                requesterName,
                requesterEmail,
                requesterSlackId,
                product.toPayload(),
                requestMessage,
                departureHub.toPayload(),
                transitHubs.stream()
                        .map(TransitHubInfo::toPayload)
                        .toList(),
                destinationAddress,
                departureHubManager.toPayload(),
                deliveryCreatedAt,
                totalExpectedDurationMinutes
        );
    }

    public record ProductInfo(
            @NotNull(message = "상품 ID는 필수입니다.")
            UUID productId,

            @NotBlank(message = "상품 이름은 필수입니다.")
            String name,

            @NotNull(message = "상품 수량은 필수입니다.")
            @Positive(message = "상품 수량은 1 이상이어야 합니다.")
            Integer quantity
    ) {

        private AiRequestPayload.ProductInfo toPayload() {
            return new AiRequestPayload.ProductInfo(
                    productId,
                    name,
                    quantity
            );
        }
    }

    public record HubInfo(
            @NotNull(message = "출발 허브 ID는 필수입니다.")
            UUID hubId,

            @NotBlank(message = "출발 허브 이름은 필수입니다.")
            String name,

            @NotBlank(message = "출발 허브 주소는 필수입니다.")
            String address
    ) {

        private AiRequestPayload.HubInfo toPayload() {
            return new AiRequestPayload.HubInfo(hubId, name, address);
        }
    }

    public record TransitHubInfo(
            @NotNull(message = "경유 순서는 필수입니다.")
            @Positive(message = "경유 순서는 1 이상이어야 합니다.")
            Integer sequence,

            @NotNull(message = "경유 허브 ID는 필수입니다.")
            UUID hubId,

            @NotBlank(message = "경유 허브 이름은 필수입니다.")
            String name,

            @NotBlank(message = "경유 허브 주소는 필수입니다.")
            String address,

            @NotNull(message = "경유 구간 예상 소요시간은 필수입니다.")
            @PositiveOrZero(message = "경유 구간 예상 소요시간은 0 이상이어야 합니다.")
            Integer expectedDurationMinutes
    ) {

        private AiRequestPayload.TransitHubInfo toPayload() {
            return new AiRequestPayload.TransitHubInfo(
                    sequence,
                    hubId,
                    name,
                    address,
                    expectedDurationMinutes
            );
        }
    }

    public record ManagerInfo(
            @NotNull(message = "담당자 ID는 필수입니다.")
            UUID userId,

            @NotBlank(message = "담당자 이름은 필수입니다.")
            String name,

            @NotBlank(message = "담당자 Slack ID는 필수입니다.")
            String slackId
    ) {

        private AiRequestPayload.ManagerInfo toPayload() {
            return new AiRequestPayload.ManagerInfo(userId, name, slackId);
        }
    }
}
