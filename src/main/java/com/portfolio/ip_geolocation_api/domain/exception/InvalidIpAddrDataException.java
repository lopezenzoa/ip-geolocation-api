package com.portfolio.ip_geolocation_api.domain.exception;

public class InvalidIpAddrDataException extends RuntimeException {

    public InvalidIpAddrDataException(String message) {
        super(message);
    }
}
