package com.portfolio.ip_geolocation_api.domain.exception;

public class IpAddrAlreadyExistsException extends RuntimeException {

    public IpAddrAlreadyExistsException(String ip) {
        super("IP address already exists: " + ip);
    }
}
