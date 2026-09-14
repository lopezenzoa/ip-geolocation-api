package com.portfolio.ip_geolocation_api.domain.exception;

public class IpAddrNotFoundException extends RuntimeException {

    public IpAddrNotFoundException(String ip) {
        super("IP address not found: " + ip);
    }
}
