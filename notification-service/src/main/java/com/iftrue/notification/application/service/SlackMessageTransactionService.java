package com.iftrue.notification.application.service;

import com.iftrue.notification.application.dto.SlackMessageDispatchTarget;
import com.iftrue.notification.domain.aialert.AiAlert;
import com.iftrue.notification.domain.aialert.AiAlertRepository;
import com.iftrue.notification.domain.slackmessage.SlackMessage;
import com.iftrue.notification.domain.slackmessage.SlackMessageRepository;
import com.iftrue.notification.domain.slackmessage.SlackMessageStatus;
import com.iftrue.notification.global.config.SlackProperties;
import com.iftrue.notification.global.exception.BusinessException;
import com.iftrue.notification.global.exception.NotificationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SlackMessageTransactionService {

    private static final String TIMEOUT_ERROR_MESSAGE =
            NotificationErrorCode.SLACK_MESSAGE_SEND_TIMEOUT.getCode()
                    + ": "
                    + NotificationErrorCode.SLACK_MESSAGE_SEND_TIMEOUT.getMessage();

    private final AiAlertRepository aiAlertRepository;
    private final SlackMessageRepository slackMessageRepository;
    private final SlackMessageFormatter slackMessageFormatter;
    private final SlackProperties slackProperties;

    @Transactional
    public void recoverTimedOut() {
        Instant timeoutThreshold = Instant.now().minus(slackProperties.processingTimeout());

        slackMessageRepository.findNextTimedOutSending(timeoutThreshold)
                .ifPresent(slackMessage -> slackMessage.fail(TIMEOUT_ERROR_MESSAGE));
    }

    @Transactional
    public Optional<SlackMessageDispatchTarget> claimForAiAlert(UUID aiAlertId) {
        return aiAlertRepository.findCompletedByIdForUpdate(aiAlertId)
                .flatMap(this::createAndClaim);
    }

    @Transactional
    public Optional<SlackMessageDispatchTarget> claimNext() {
        return aiAlertRepository.findNextCompletedForSlackMessage()
                .flatMap(this::createAndClaim);
    }

    @Transactional
    public void complete(UUID slackMessageId, Instant sentAt) {
        SlackMessage slackMessage = findById(slackMessageId);

        if (slackMessage.getStatus() != SlackMessageStatus.SENDING) {
            return;
        }

        slackMessage.complete(sentAt);
    }

    @Transactional
    public void fail(UUID slackMessageId, String errorMessage) {
        SlackMessage slackMessage = findById(slackMessageId);

        if (slackMessage.getStatus() != SlackMessageStatus.SENDING) {
            return;
        }

        slackMessage.fail(errorMessage);
    }

    private Optional<SlackMessageDispatchTarget> createAndClaim(AiAlert aiAlert) {
        if (slackMessageRepository.existsByAiAlert_Id(aiAlert.getId())) {
            return Optional.empty();
        }

        SlackMessage slackMessage = create(aiAlert);

        return Optional.of(new SlackMessageDispatchTarget(
                slackMessage.getId(),
                slackMessage.getSlackReceiverId(),
                slackMessage.getMessage()
        ));
    }

    private SlackMessage create(AiAlert aiAlert) {
        String receiverId = aiAlert.getRequestPayload()
                .departureHubManager()
                .slackId();
        String message = slackMessageFormatter.format(aiAlert);

        return slackMessageRepository.save(
                SlackMessage.create(aiAlert, receiverId, message)
        );
    }

    private SlackMessage findById(UUID slackMessageId) {
        return slackMessageRepository.findById(slackMessageId)
                .orElseThrow(() -> new BusinessException(
                        NotificationErrorCode.NOTIFICATION_NOT_FOUND
                ));
    }
}
