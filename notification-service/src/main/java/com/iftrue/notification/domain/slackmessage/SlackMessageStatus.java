package com.iftrue.notification.domain.slackmessage;

public enum SlackMessageStatus {
    WAITING_CONFIRMATION,
    SENDING,
    SENT,
    RETRY_WAIT,
    FAILED,
    CANCELED
}
