package com.portfolio.ip_geolocation_api.infrastructure.adapter.out.rest;

public class GeolocationRateLimitedException extends RuntimeException {

    public GeolocationRateLimitedException(String message, Throwable cause) {
        super(message, cause);
    }
}