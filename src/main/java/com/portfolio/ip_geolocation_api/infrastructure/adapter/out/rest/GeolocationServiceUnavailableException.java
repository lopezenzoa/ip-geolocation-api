package com.portfolio.ip_geolocation_api.infrastructure.adapter.out.rest;

public class GeolocationServiceUnavailableException extends RuntimeException {

    public GeolocationServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}