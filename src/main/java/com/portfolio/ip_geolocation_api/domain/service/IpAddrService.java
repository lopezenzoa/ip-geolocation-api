package com.portfolio.ip_geolocation_api.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.portfolio.ip_geolocation_api.application.port.out.GetIpLocationPort;
import com.portfolio.ip_geolocation_api.application.port.out.IpAddrPersistencePort;
import com.portfolio.ip_geolocation_api.domain.exception.IpAddrAlreadyExistsException;
import com.portfolio.ip_geolocation_api.domain.exception.IpAddrNotFoundException;
import com.portfolio.ip_geolocation_api.domain.exception.InvalidIpAddrDataException;
import com.portfolio.ip_geolocation_api.domain.model.IpAddr;

@Service
public class IpAddrService {

    private final IpAddrPersistencePort persistencePort;
    private final GetIpLocationPort getIpLocationPort;

    public IpAddrService(IpAddrPersistencePort persistencePort, GetIpLocationPort getIpLocationPort) {
        this.persistencePort = persistencePort;
        this.getIpLocationPort = getIpLocationPort;
    }

    public IpAddr create(IpAddr ipAddr) {
        if (ipAddr == null) {
            throw new InvalidIpAddrDataException("IpAddr cannot be null");
        }
        if (ipAddr.getIp() == null || ipAddr.getIp().isBlank()) {
            throw new InvalidIpAddrDataException("IP address is required");
        }
        if (persistencePort.findByIp(ipAddr.getIp()).isPresent()) {
            throw new IpAddrAlreadyExistsException(ipAddr.getIp());
        }
        persistencePort.save(ipAddr);
        return ipAddr;
    }

    public Optional<IpAddr> readByIp(String ip) {
        if (ip == null || ip.isBlank()) {
            throw new InvalidIpAddrDataException("IP address is required");
        }
        Optional<IpAddr> cached = persistencePort.findByIp(ip);
        if (cached.isPresent()) {
            return cached;
        }
        return getIpLocationPort.getIpLocation(ip)
                .map(fetched -> {
                    persistencePort.save(fetched);
                    return fetched;
                });
    }

    public List<IpAddr> listAll() {
        return persistencePort.findAll();
    }

    public IpAddr update(String ip, IpAddr updatedIpAddr) {
        if (ip == null || ip.isBlank()) {
            throw new InvalidIpAddrDataException("IP address is required");
        }
        if (updatedIpAddr == null) {
            throw new InvalidIpAddrDataException("Updated IpAddr cannot be null");
        }
        if (persistencePort.findByIp(ip).isEmpty()) {
            throw new IpAddrNotFoundException(ip);
        }
        updatedIpAddr.setIp(ip);
        persistencePort.save(updatedIpAddr);
        return updatedIpAddr;
    }

    public void delete(String ip) {
        if (ip == null || ip.isBlank()) {
            throw new InvalidIpAddrDataException("IP address is required");
        }
        if (persistencePort.findByIp(ip).isEmpty()) {
            throw new IpAddrNotFoundException(ip);
        }
        persistencePort.delete(ip);
    }
}