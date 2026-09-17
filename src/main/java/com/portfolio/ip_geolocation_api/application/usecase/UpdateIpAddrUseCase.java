package com.portfolio.ip_geolocation_api.application.usecase;

import org.springframework.stereotype.Component;

import com.portfolio.ip_geolocation_api.application.port.in.UpdateIpAddrCommand;
import com.portfolio.ip_geolocation_api.domain.model.IpAddr;
import com.portfolio.ip_geolocation_api.domain.service.IpAddrService;

@Component
public class UpdateIpAddrUseCase implements UpdateIpAddrCommand {

    private final IpAddrService ipAddrService;

    public UpdateIpAddrUseCase(IpAddrService ipAddrService) {
        this.ipAddrService = ipAddrService;
    }

    @Override
    public IpAddr execute(String ip, IpAddr updatedIpAddr) {
        return ipAddrService.update(ip, updatedIpAddr);
    }
}