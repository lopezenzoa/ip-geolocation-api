package com.portfolio.ip_geolocation_api.infrastructure.adapter.in.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateIpAddrRequest {

    @NotBlank(message = "IP address is required")
    @Pattern(regexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$"
            + "|^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$"
            + "|^([0-9a-fA-F]{1,4}:){1,7}:$"
            + "|^([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}$"
            + "|^([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}$"
            + "|^([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}$"
            + "|^([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}$"
            + "|^([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}$"
            + "|^[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})$"
            + "|^:((:[0-9a-fA-F]{1,4}){1,7}|:)$",
            message = "IP address must be a valid IPv4 or IPv6 address")
    private String ip;
}