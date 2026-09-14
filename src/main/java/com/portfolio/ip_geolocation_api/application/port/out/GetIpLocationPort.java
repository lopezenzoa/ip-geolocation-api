package com.portfolio.ip_geolocation_api.application.port.out;

import java.util.Optional;

import com.portfolio.ip_geolocation_api.domain.model.IpAddr;

public interface GetIpLocationPort {

    Optional<IpAddr> getIpLocation(String ip);
}
