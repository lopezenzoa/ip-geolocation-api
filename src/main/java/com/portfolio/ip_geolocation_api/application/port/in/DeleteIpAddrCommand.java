package com.portfolio.ip_geolocation_api.application.port.in;

public interface DeleteIpAddrCommand {

    void execute(String ip);
}