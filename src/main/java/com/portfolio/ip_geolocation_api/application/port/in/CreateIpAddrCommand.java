package com.portfolio.ip_geolocation_api.application.port.in;

import com.portfolio.ip_geolocation_api.domain.model.IpAddr;

public interface CreateIpAddrCommand {

    IpAddr execute(String ip);
}