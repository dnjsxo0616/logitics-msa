package com.iftrue.notification.application.service;

import com.iftrue.notification.application.dto.SlackMessageDispatchTarget;
import com.iftrue.notification.domain.aialert.AiAlert;
import com.iftrue.notification.domain.aialert.AiAlertRepository;
import com.iftrue.notification.domain.slackmessage.SlackMessage;
import com.iftrue.notification.domain.slackmessage.SlackMessageRepository;
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
    public Optional<SlackMessageDispatchTarget> prepare(UUID aiAlertId) {
        if (slackMessageRepository.existsByAiAlert_Id(aiAlertId)) {
            return Optional.empty();
        }

        return aiAlertRepository.findCompletedByIdForUpdate(aiAlertId)
                .map(this::toDispatchTarget);
    }

    @Transactional
    public void complete(UUID aiAlertId, String receiverId, String message, Instant sentAt) {
        AiAlert aiAlert = aiAlertRepository.getReferenceById(aiAlertId);
        SlackMessage slackMessage = SlackMessage.create(aiAlert, receiverId, message);
        slackMessage.complete(sentAt);

        slackMessageRepository.save(slackMessage);
    }

    @Transactional
    public void fail(UUID aiAlertId, String receiverId, String message, String errorMessage) {
        AiAlert aiAlert = aiAlertRepository.getReferenceById(aiAlertId);
        SlackMessage slackMessage = SlackMessage.create(aiAlert, receiverId, message);
        slackMessage.fail(errorMessage);

        slackMessageRepository.save(slackMessage);
    }

    private SlackMessageDispatchTarget toDispatchTarget(AiAlert aiAlert) {
        String receiverId = aiAlert.getRequestPayload()
                .departureHubManager()
                .slackId();
        String message = slackMessageFormatter.format(aiAlert);

        return new SlackMessageDispatchTarget(aiAlert.getId(), receiverId, message);
    }
}
