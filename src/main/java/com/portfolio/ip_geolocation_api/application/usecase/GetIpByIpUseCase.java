package com.portfolio.ip_geolocation_api.application.usecase;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.portfolio.ip_geolocation_api.application.port.in.GetIpByIpQuery;
import com.portfolio.ip_geolocation_api.domain.model.IpAddr;
import com.portfolio.ip_geolocation_api.domain.service.IpAddrService;

@Component
public class GetIpByIpUseCase implements GetIpByIpQuery {

    private final IpAddrService ipAddrService;

    public GetIpByIpUseCase(IpAddrService ipAddrService) {
        this.ipAddrService = ipAddrService;
    }

    @Override
    public Optional<IpAddr> execute(String ip) {
        return ipAddrService.readByIp(ip);
    }
}