package com.portfolio.ip_geolocation_api.infrastructure.adapter.out.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

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
        } catch (HttpClientErrorException e) {
            return handleClientError(e);
        } catch (HttpServerErrorException e) {
            throw new GeolocationServiceUnavailableException(
                    "Geolocation service returned an error: " + e.getStatusCode(), e);
        } catch (RestClientException e) {
            throw new GeolocationServiceUnavailableException(
                    "Failed to reach geolocation service: " + e.getMessage(), e);
        }
    }

    private Optional<IpGeolocationResponse> handleClientError(HttpClientErrorException e) {
        int status = e.getStatusCode().value();
        if (status == 401 || status == 403) {
            throw new GeolocationAuthenticationException(
                    "Geolocation service rejected the API key (HTTP " + status + ")", e);
        }
        if (status == 429) {
            throw new GeolocationRateLimitedException(
                    "Geolocation service rate limit exceeded (HTTP 429)", e);
        }
        if (status == 404) {
            return Optional.empty();
        }
        throw new GeolocationServiceUnavailableException(
                "Geolocation service returned an unexpected error (HTTP " + status + ")", e);
    }
}