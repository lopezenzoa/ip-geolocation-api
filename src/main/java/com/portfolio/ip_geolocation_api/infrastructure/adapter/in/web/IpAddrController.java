package com.portfolio.ip_geolocation_api.infrastructure.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.ip_geolocation_api.application.port.in.GetIpByIpQuery;
import com.portfolio.ip_geolocation_api.domain.model.IpAddr;

@RestController
@RequestMapping("/api/ip")
public class IpAddrController {

    private final GetIpByIpQuery getIpByIpQuery;

    public IpAddrController(GetIpByIpQuery getIpByIpQuery) {
        this.getIpByIpQuery = getIpByIpQuery;
    }

    @GetMapping("/{ip}")
    public ResponseEntity<IpAddr> getIp(@PathVariable String ip) {
        return getIpByIpQuery.execute(ip)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
