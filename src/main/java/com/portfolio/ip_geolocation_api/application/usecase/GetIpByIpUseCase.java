package com.portfolio.ip_geolocation_api.application.usecase;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.portfolio.ip_geolocation_api.application.port.in.GetIpByIpQuery;
import com.portfolio.ip_geolocation_api.application.port.out.GetIpLocationPort;
import com.portfolio.ip_geolocation_api.domain.model.IpAddr;
import com.portfolio.ip_geolocation_api.domain.service.IpAddrService;

@Component
public class GetIpByIpUseCase implements GetIpByIpQuery {

    private final IpAddrService ipAddrService;
    private final GetIpLocationPort getIpLocationPort;

    public GetIpByIpUseCase(IpAddrService ipAddrService, GetIpLocationPort getIpLocationPort) {
        this.ipAddrService = ipAddrService;
        this.getIpLocationPort = getIpLocationPort;
    }

    @Override
    public Optional<IpAddr> execute(String ip) {
        return ipAddrService.readByIp(ip)
                .or(() -> getIpLocationPort.getIpLocation(ip));
    }
}