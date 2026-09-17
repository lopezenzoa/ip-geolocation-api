package com.portfolio.ip_geolocation_api.application.port.in;

import java.util.List;

import com.portfolio.ip_geolocation_api.domain.model.IpAddr;

public interface ListAllIpAddrQuery {

    List<IpAddr> execute();
}