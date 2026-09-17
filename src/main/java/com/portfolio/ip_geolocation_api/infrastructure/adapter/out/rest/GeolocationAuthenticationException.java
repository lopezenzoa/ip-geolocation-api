package com.portfolio.ip_geolocation_api.infrastructure.adapter.out.rest;

public class GeolocationAuthenticationException extends RuntimeException {

    public GeolocationAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}