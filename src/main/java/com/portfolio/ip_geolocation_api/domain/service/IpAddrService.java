package com.portfolio.ip_geolocation_api.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.portfolio.ip_geolocation_api.application.port.out.GetIpLocationPort;
import com.portfolio.ip_geolocation_api.domain.exception.IpAddrAlreadyExistsException;
import com.portfolio.ip_geolocation_api.domain.exception.IpAddrNotFoundException;
import com.portfolio.ip_geolocation_api.domain.exception.InvalidIpAddrDataException;
import com.portfolio.ip_geolocation_api.domain.model.IpAddr;

@Service
public class IpAddrService {

    private final List<IpAddr> ipAddrStore = new ArrayList<>();
    private final GetIpLocationPort getIpLocationPort;

    public IpAddrService(GetIpLocationPort getIpLocationPort) {
        this.getIpLocationPort = getIpLocationPort;
    }

    public IpAddr create(IpAddr ipAddr) {
        if (ipAddr == null) {
            throw new InvalidIpAddrDataException("IpAddr cannot be null");
        }
        if (ipAddr.getIp() == null || ipAddr.getIp().isBlank()) {
            throw new InvalidIpAddrDataException("IP address is required");
        }
        boolean alreadyExists = ipAddrStore.stream()
                .anyMatch(existing -> existing.getIp().equals(ipAddr.getIp()));
        if (alreadyExists) {
            throw new IpAddrAlreadyExistsException(ipAddr.getIp());
        }
        ipAddrStore.add(ipAddr);
        return ipAddr;
    }

    public Optional<IpAddr> readByIp(String ip) {
        if (ip == null || ip.isBlank()) {
            return Optional.empty();
        }
        return findInStore(ip)
                .or(() -> getIpLocationPort.getIpLocation(ip));
    }

    private Optional<IpAddr> findInStore(String ip) {
        return ipAddrStore.stream()
                .filter(ipAddr -> ipAddr.getIp().equals(ip))
                .findFirst();
    }

    public List<IpAddr> listAll() {
        return List.copyOf(ipAddrStore);
    }

    public IpAddr update(String ip, IpAddr updatedIpAddr) {
        if (ip == null || ip.isBlank()) {
            throw new InvalidIpAddrDataException("IP address is required");
        }
        if (updatedIpAddr == null) {
            throw new InvalidIpAddrDataException("Updated IpAddr cannot be null");
        }
        for (int i = 0; i < ipAddrStore.size(); i++) {
            if (ipAddrStore.get(i).getIp().equals(ip)) {
                updatedIpAddr.setIp(ip);
                ipAddrStore.set(i, updatedIpAddr);
                return updatedIpAddr;
            }
        }
        throw new IpAddrNotFoundException(ip);
    }

    public void delete(String ip) {
        if (ip == null || ip.isBlank()) {
            throw new InvalidIpAddrDataException("IP address is required");
        }
        boolean removed = ipAddrStore.removeIf(ipAddr -> ipAddr.getIp().equals(ip));
        if (!removed) {
            throw new IpAddrNotFoundException(ip);
        }
    }
}
