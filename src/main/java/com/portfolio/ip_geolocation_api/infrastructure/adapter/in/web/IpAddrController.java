package com.portfolio.ip_geolocation_api.infrastructure.adapter.in.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.ip_geolocation_api.application.port.in.CreateIpAddrCommand;
import com.portfolio.ip_geolocation_api.application.port.in.DeleteIpAddrCommand;
import com.portfolio.ip_geolocation_api.application.port.in.GetIpByIpQuery;
import com.portfolio.ip_geolocation_api.application.port.in.ListAllIpAddrQuery;
import com.portfolio.ip_geolocation_api.application.port.in.UpdateIpAddrCommand;
import com.portfolio.ip_geolocation_api.domain.exception.IpAddrNotFoundException;
import com.portfolio.ip_geolocation_api.domain.model.IpAddr;

@RestController
@RequestMapping("/api/ip")
public class IpAddrController {

    private final GetIpByIpQuery getIpByIpQuery;
    private final CreateIpAddrCommand createIpAddrCommand;
    private final UpdateIpAddrCommand updateIpAddrCommand;
    private final DeleteIpAddrCommand deleteIpAddrCommand;
    private final ListAllIpAddrQuery listAllIpAddrQuery;

    public IpAddrController(
            GetIpByIpQuery getIpByIpQuery,
            CreateIpAddrCommand createIpAddrCommand,
            UpdateIpAddrCommand updateIpAddrCommand,
            DeleteIpAddrCommand deleteIpAddrCommand,
            ListAllIpAddrQuery listAllIpAddrQuery) {
        this.getIpByIpQuery = getIpByIpQuery;
        this.createIpAddrCommand = createIpAddrCommand;
        this.updateIpAddrCommand = updateIpAddrCommand;
        this.deleteIpAddrCommand = deleteIpAddrCommand;
        this.listAllIpAddrQuery = listAllIpAddrQuery;
    }

    @PostMapping
    public ResponseEntity<IpAddr> create(@RequestBody CreateIpAddrRequest request) {
        IpAddr created = createIpAddrCommand.execute(request.getIp());
        return ResponseEntity.created(URI.create("/api/ip/" + created.getIp()))
                .body(created);
    }

    @GetMapping
    public ResponseEntity<List<IpAddr>> listAll() {
        return ResponseEntity.ok(listAllIpAddrQuery.execute());
    }

    @GetMapping("/{ip}")
    public ResponseEntity<IpAddr> getIp(@PathVariable String ip) {
        return ResponseEntity.ok(getIpByIpQuery.execute(ip)
                .orElseThrow(() -> new IpAddrNotFoundException(ip)));
    }

    @PutMapping("/{ip}")
    public ResponseEntity<IpAddr> update(@PathVariable String ip, @RequestBody IpAddr updatedIpAddr) {
        return ResponseEntity.ok(updateIpAddrCommand.execute(ip, updatedIpAddr));
    }

    @DeleteMapping("/{ip}")
    public ResponseEntity<Void> delete(@PathVariable String ip) {
        deleteIpAddrCommand.execute(ip);
        return ResponseEntity.noContent().build();
    }
}