package com.portfolio.ip_geolocation_api.application.usecase;

import org.springframework.stereotype.Component;

import com.portfolio.ip_geolocation_api.application.port.in.DeleteIpAddrCommand;
import com.portfolio.ip_geolocation_api.domain.service.IpAddrService;

@Component
public class DeleteIpAddrUseCase implements DeleteIpAddrCommand {

    private final IpAddrService ipAddrService;

    public DeleteIpAddrUseCase(IpAddrService ipAddrService) {
        this.ipAddrService = ipAddrService;
    }

    @Override
    public void execute(String ip) {
        ipAddrService.delete(ip);
    }
}