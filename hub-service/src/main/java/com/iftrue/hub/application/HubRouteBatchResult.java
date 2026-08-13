package com.iftrue.hub.application;

public record HubRouteBatchResult(
        int created,
        int updated,
        int failed
) {
}
