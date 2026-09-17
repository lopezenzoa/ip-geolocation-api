package com.portfolio.ip_geolocation_api.application.usecase;

import org.springframework.stereotype.Component;

import com.portfolio.ip_geolocation_api.application.port.in.CreateIpAddrCommand;
import com.portfolio.ip_geolocation_api.application.port.out.GetIpLocationPort;
import com.portfolio.ip_geolocation_api.domain.exception.IpAddrNotFoundException;
import com.portfolio.ip_geolocation_api.domain.model.IpAddr;
import com.portfolio.ip_geolocation_api.domain.service.IpAddrService;

@Component
public class CreateIpAddrUseCase implements CreateIpAddrCommand {

    private final GetIpLocationPort getIpLocationPort;
    private final IpAddrService ipAddrService;

    public CreateIpAddrUseCase(GetIpLocationPort getIpLocationPort, IpAddrService ipAddrService) {
        this.getIpLocationPort = getIpLocationPort;
        this.ipAddrService = ipAddrService;
    }

    @Override
    public IpAddr execute(String ip) {
        IpAddr fetched = getIpLocationPort.getIpLocation(ip)
                .orElseThrow(() -> new IpAddrNotFoundException(ip));
        return ipAddrService.create(fetched);
    }
}