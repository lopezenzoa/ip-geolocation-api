package com.portfolio.ip_geolocation_api.infrastructure.adapter.out.rest;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.portfolio.ip_geolocation_api.application.port.out.GetIpLocationPort;
import com.portfolio.ip_geolocation_api.domain.model.IpAddr;
import com.portfolio.ip_geolocation_api.domain.model.Location;

@Component
public class IpGeolocationAdapter implements GetIpLocationPort {

    private final IpGeolocationClient client;

    public IpGeolocationAdapter(IpGeolocationClient client) {
        this.client = client;
    }

    @Override
    public Optional<IpAddr> getIpLocation(String ip) {
        return client.fetchIpLocation(ip)
                .map(this::toDomain);
    }

    private IpAddr toDomain(IpGeolocationResponse response) {
        Location location = new Location();
        location.setContinentCode(response.getContinentCode());
        location.setContinentName(response.getContinentName());
        location.setCountryCode(response.getCountryCode3());
        location.setCountryName(response.getCountryName());
        location.setCountryCapital(response.getCountryCapital());
        location.setStateProv(response.getStateProv());
        location.setDistrict(response.getDistrict());
        location.setCity(response.getCity());
        location.setLatitude(response.getLatitude());
        location.setLongitude(response.getLongitude());
        location.setCountryFlag(response.getCountryFlag());

        IpAddr ipAddr = new IpAddr();
        ipAddr.setIp(response.getIp());
        ipAddr.setLocation(location);
        return ipAddr;
    }
}
