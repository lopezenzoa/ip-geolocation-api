package com.portfolio.ip_geolocation_api.application.port.out;

import java.util.List;
import java.util.Optional;

import com.portfolio.ip_geolocation_api.domain.model.IpAddr;

public interface IpAddrPersistencePort {

    Optional<IpAddr> findByIp(String ip);

    List<IpAddr> findAll();

    void save(IpAddr ipAddr);

    void delete(String ip);
}