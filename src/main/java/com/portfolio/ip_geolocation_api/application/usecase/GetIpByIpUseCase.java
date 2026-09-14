package com.portfolio.ip_geolocation_api.application.usecase;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.portfolio.ip_geolocation_api.application.port.in.GetIpByIpQuery;
import com.portfolio.ip_geolocation_api.application.port.out.GetIpLocationPort;
import com.portfolio.ip_geolocation_api.domain.model.IpAddr;

@Component
public class GetIpByIpUseCase implements GetIpByIpQuery {

    private final GetIpLocationPort getIpLocationPort;

    public GetIpByIpUseCase(GetIpLocationPort getIpLocationPort) {
        this.getIpLocationPort = getIpLocationPort;
    }

    @Override
    public Optional<IpAddr> execute(String ip) {
        return getIpLocationPort.getIpLocation(ip);
    }
}
