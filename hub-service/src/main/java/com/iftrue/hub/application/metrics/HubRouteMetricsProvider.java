package com.iftrue.hub.application.metrics;

public interface HubRouteMetricsProvider {
    HubRouteMetrics fetch(HubCoordinate departure, HubCoordinate arrival);
}
