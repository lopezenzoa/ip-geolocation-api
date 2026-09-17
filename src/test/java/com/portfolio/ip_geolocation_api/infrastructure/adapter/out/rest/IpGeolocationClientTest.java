package com.portfolio.ip_geolocation_api.infrastructure.adapter.out.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class IpGeolocationClientTest {

    private static final String URL = "https://api.ipgeolocation.io/ipgeo";

    private MockRestServiceServer server;
    private IpGeolocationClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder()
                .configureMessageConverters(converters -> converters.withJsonConverter(
                        new JacksonJsonHttpMessageConverter()));
        server = MockRestServiceServer.bindTo(builder).build();
        client = new IpGeolocationClient(builder, "https://api.ipgeolocation.io", "test-key");
    }

    @Test
    void notFoundReturnsEmpty() {
        server.expect(requestTo(URL + "?ip=8.8.8.8&apiKey=test-key"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        Optional<IpGeolocationResponse> result = client.fetchIpLocation("8.8.8.8");

        assertThat(result).isEmpty();
    }

    @Test
    void unauthorizedThrowsAuthentication() {
        server.expect(requestTo(URL + "?ip=8.8.8.8&apiKey=test-key"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        assertThatThrownBy(() -> client.fetchIpLocation("8.8.8.8"))
                .isInstanceOf(GeolocationAuthenticationException.class);
    }

    @Test
    void forbiddenThrowsAuthentication() {
        server.expect(requestTo(URL + "?ip=8.8.8.8&apiKey=test-key"))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));

        assertThatThrownBy(() -> client.fetchIpLocation("8.8.8.8"))
                .isInstanceOf(GeolocationAuthenticationException.class);
    }

    @Test
    void rateLimitedThrowsRateLimited() {
        server.expect(requestTo(URL + "?ip=8.8.8.8&apiKey=test-key"))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

        assertThatThrownBy(() -> client.fetchIpLocation("8.8.8.8"))
                .isInstanceOf(GeolocationRateLimitedException.class);
    }

    @Test
    void serverErrorThrowsServiceUnavailable() {
        server.expect(requestTo(URL + "?ip=8.8.8.8&apiKey=test-key"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.fetchIpLocation("8.8.8.8"))
                .isInstanceOf(GeolocationServiceUnavailableException.class);
    }

    @Test
    void successReturnsIp() {
        server.expect(requestTo(URL + "?ip=8.8.8.8&apiKey=test-key"))
                .andRespond(withSuccess(
                        "{\"ip\":\"8.8.8.8\",\"continent_code\":\"AS\",\"country_name\":\"United States\"}",
                        MediaType.APPLICATION_JSON));

        Optional<IpGeolocationResponse> result = client.fetchIpLocation("8.8.8.8");

        assertThat(result).isPresent();
        assertThat(result.get().getIp()).isEqualTo("8.8.8.8");
        assertThat(result.get().getContinentCode()).isEqualTo("AS");
    }
}