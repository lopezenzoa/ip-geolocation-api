package com.portfolio.ip_geolocation_api.application.usecase;

import java.util.List;

import org.springframework.stereotype.Component;

import com.portfolio.ip_geolocation_api.application.port.in.ListAllIpAddrQuery;
import com.portfolio.ip_geolocation_api.domain.model.IpAddr;
import com.portfolio.ip_geolocation_api.domain.service.IpAddrService;

@Component
public class ListAllIpAddrUseCase implements ListAllIpAddrQuery {

    private final IpAddrService ipAddrService;

    public ListAllIpAddrUseCase(IpAddrService ipAddrService) {
        this.ipAddrService = ipAddrService;
    }

    @Override
    public List<IpAddr> execute() {
        return ipAddrService.listAll();
    }
}