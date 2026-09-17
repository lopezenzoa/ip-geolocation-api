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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.portfolio.ip_geolocation_api.application.port.in.CreateIpAddrCommand;
import com.portfolio.ip_geolocation_api.application.port.in.DeleteIpAddrCommand;
import com.portfolio.ip_geolocation_api.application.port.in.GetIpByIpQuery;
import com.portfolio.ip_geolocation_api.application.port.in.ListAllIpAddrQuery;
import com.portfolio.ip_geolocation_api.application.port.in.UpdateIpAddrCommand;
import com.portfolio.ip_geolocation_api.domain.exception.IpAddrNotFoundException;
import com.portfolio.ip_geolocation_api.domain.model.IpAddr;

@Tag(name = "IpAddr", description = "Manage IP address geolocation records")
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

    @Operation(summary = "Create an IpAddr",
            description = "Fetches the geolocation for the given IP from the external API and stores it locally. "
                    + "Returns 409 if the IP already exists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "IpAddr created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or blank IP address"),
            @ApiResponse(responseCode = "404", description = "Geolocation lookup failed for the IP"),
            @ApiResponse(responseCode = "409", description = "IP address already exists")
    })
    @PostMapping
    public ResponseEntity<IpAddr> create(@RequestBody CreateIpAddrRequest request) {
        IpAddr created = createIpAddrCommand.execute(request.getIp());
        return ResponseEntity.created(URI.create("/api/ip/" + created.getIp()))
                .body(created);
    }

    @Operation(summary = "List all IpAddr records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all stored IpAddr records")
    })
    @GetMapping
    public ResponseEntity<List<IpAddr>> listAll() {
        return ResponseEntity.ok(listAllIpAddrQuery.execute());
    }

    @Operation(summary = "Get an IpAddr by IP address",
            description = "Returns the record from the local store, or fetches it from the external API if not stored.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "IpAddr found"),
            @ApiResponse(responseCode = "404", description = "IP address not found")
    })
    @GetMapping("/{ip}")
    public ResponseEntity<IpAddr> getIp(
            @Parameter(description = "IP address to look up", example = "8.8.8.8")
            @PathVariable String ip) {
        return ResponseEntity.ok(getIpByIpQuery.execute(ip)
                .orElseThrow(() -> new IpAddrNotFoundException(ip)));
    }

    @Operation(summary = "Update an existing IpAddr")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "IpAddr updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "IP address not found")
    })
    @PutMapping("/{ip}")
    public ResponseEntity<IpAddr> update(
            @Parameter(description = "IP address to update", example = "8.8.8.8")
            @PathVariable String ip,
            @RequestBody IpAddr updatedIpAddr) {
        return ResponseEntity.ok(updateIpAddrCommand.execute(ip, updatedIpAddr));
    }

    @Operation(summary = "Delete an IpAddr by IP address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "IpAddr deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or blank IP address"),
            @ApiResponse(responseCode = "404", description = "IP address not found")
    })
    @DeleteMapping("/{ip}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "IP address to delete", example = "8.8.8.8")
            @PathVariable String ip) {
        deleteIpAddrCommand.execute(ip);
        return ResponseEntity.noContent().build();
    }
}