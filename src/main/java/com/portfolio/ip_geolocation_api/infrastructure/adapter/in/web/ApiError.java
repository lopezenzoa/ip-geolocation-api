package com.portfolio.ip_geolocation_api.infrastructure.adapter.in.web;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        List<String> details,
        String path) {
}