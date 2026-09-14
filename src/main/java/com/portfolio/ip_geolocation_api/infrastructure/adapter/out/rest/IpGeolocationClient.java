package com.portfolio.ip_geolocation_api.infrastructure.adapter.out.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class IpGeolocationClient {

    private final RestClient restClient;
    private final String apiKey;

    public IpGeolocationClient(
            RestClient.Builder restClientBuilder,
            @Value("${geolocation.api.base-url}") String baseUrl,
            @Value("${geolocation.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    public Optional<IpGeolocationResponse> fetchIpLocation(String ip) {
        try {
            IpGeolocationResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.ipgeolocation.io")
                            .path("/ipgeo")
                            .queryParam("ip", ip)
                            .queryParam("apiKey", apiKey)
                            .build())
                    .retrieve()
                    .body(IpGeolocationResponse.class);
            return Optional.ofNullable(response);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}