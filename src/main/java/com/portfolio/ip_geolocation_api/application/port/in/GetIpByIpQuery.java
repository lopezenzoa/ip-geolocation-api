package com.portfolio.ip_geolocation_api.application.port.in;

import java.util.Optional;

import com.portfolio.ip_geolocation_api.domain.model.IpAddr;

public interface GetIpByIpQuery {

    Optional<IpAddr> execute(String ip);
}
