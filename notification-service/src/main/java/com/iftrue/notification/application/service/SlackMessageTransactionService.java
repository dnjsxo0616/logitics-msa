package com.iftrue.notification.application.service;

import com.iftrue.notification.application.dto.SlackMessageDispatchTarget;
import com.iftrue.notification.domain.aialert.AiAlert;
import com.iftrue.notification.domain.aialert.AiAlertRepository;
import com.iftrue.notification.domain.slackmessage.SlackMessage;
import com.iftrue.notification.domain.slackmessage.SlackMessageRepository;
import com.iftrue.notification.domain.slackmessage.SlackMessageStatus;
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

    private final AiAlertRepository aiAlertRepository;
    private final SlackMessageRepository slackMessageRepository;
    private final SlackMessageFormatter slackMessageFormatter;

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
        SlackMessage slackMessage = slackMessageRepository
                .findByAiAlert_Id(aiAlert.getId())
                .orElseGet(() -> create(aiAlert));

        if (slackMessage.getStatus() != SlackMessageStatus.WAITING_CONFIRMATION) {
            return Optional.empty();
        }

        slackMessage.startSending();

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
