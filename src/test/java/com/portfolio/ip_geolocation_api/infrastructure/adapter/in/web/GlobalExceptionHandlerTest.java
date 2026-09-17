package com.portfolio.ip_geolocation_api.infrastructure.adapter.in.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.portfolio.ip_geolocation_api.application.port.in.CreateIpAddrCommand;
import com.portfolio.ip_geolocation_api.application.port.in.DeleteIpAddrCommand;
import com.portfolio.ip_geolocation_api.application.port.in.GetIpByIpQuery;
import com.portfolio.ip_geolocation_api.application.port.in.ListAllIpAddrQuery;
import com.portfolio.ip_geolocation_api.application.port.in.UpdateIpAddrCommand;
import com.portfolio.ip_geolocation_api.domain.exception.IpAddrAlreadyExistsException;
import com.portfolio.ip_geolocation_api.domain.exception.InvalidIpAddrDataException;
import com.portfolio.ip_geolocation_api.domain.model.IpAddr;
import com.portfolio.ip_geolocation_api.infrastructure.adapter.out.persistence.IpCachePersistenceException;
import com.portfolio.ip_geolocation_api.infrastructure.adapter.out.rest.GeolocationAuthenticationException;
import com.portfolio.ip_geolocation_api.infrastructure.adapter.out.rest.GeolocationRateLimitedException;
import com.portfolio.ip_geolocation_api.infrastructure.adapter.out.rest.GeolocationServiceUnavailableException;

@WebMvcTest(IpAddrController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetIpByIpQuery getIpByIpQuery;

    @MockitoBean
    private CreateIpAddrCommand createIpAddrCommand;

    @MockitoBean
    private UpdateIpAddrCommand updateIpAddrCommand;

    @MockitoBean
    private DeleteIpAddrCommand deleteIpAddrCommand;

    @MockitoBean
    private ListAllIpAddrQuery listAllIpAddrQuery;

    @Test
    void notFoundReturnsStructured404() throws Exception {
        when(getIpByIpQuery.execute("8.8.8.8")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ip/8.8.8.8"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("IP_NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/api/ip/8.8.8.8"));
    }

    @Test
    void alreadyExistsReturns409() throws Exception {
        when(createIpAddrCommand.execute("8.8.8.8")).thenThrow(new IpAddrAlreadyExistsException("8.8.8.8"));

        mockMvc.perform(post("/api/ip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ip\":\"8.8.8.8\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("IP_ALREADY_EXISTS"));
    }

    @Test
    void invalidDataReturns400() throws Exception {
        when(createIpAddrCommand.execute("8.8.8.8")).thenThrow(new InvalidIpAddrDataException("IP address is required"));

        mockMvc.perform(post("/api/ip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ip\":\"8.8.8.8\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_IP_DATA"));
    }

    @Test
    void beanValidationFailsReturns400() throws Exception {
        mockMvc.perform(post("/api/ip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ip\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[0]").isNotEmpty());
    }

    @Test
    void malformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/api/ip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ip\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
    }

    @Test
    void rateLimitedReturns429() throws Exception {
        when(getIpByIpQuery.execute("8.8.8.8"))
                .thenThrow(new GeolocationRateLimitedException("rate limited", new RuntimeException()));

        mockMvc.perform(get("/api/ip/8.8.8.8"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.code").value("GEOLOCATION_RATE_LIMITED"));
    }

    @Test
    void serviceUnavailableReturns503() throws Exception {
        when(getIpByIpQuery.execute("8.8.8.8"))
                .thenThrow(new GeolocationServiceUnavailableException("down", new RuntimeException()));

        mockMvc.perform(get("/api/ip/8.8.8.8"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("GEOLOCATION_UNAVAILABLE"));
    }

    @Test
    void authenticationFailureReturns500() throws Exception {
        when(getIpByIpQuery.execute("8.8.8.8"))
                .thenThrow(new GeolocationAuthenticationException("bad key", new RuntimeException()));

        mockMvc.perform(get("/api/ip/8.8.8.8"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("GEOLOCATION_AUTH_FAILED"));
    }

    @Test
    void cachePersistenceFailureReturns500() throws Exception {
        when(getIpByIpQuery.execute("8.8.8.8"))
                .thenThrow(new IpCachePersistenceException("write failed", new RuntimeException()));

        mockMvc.perform(get("/api/ip/8.8.8.8"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("CACHE_PERSISTENCE_ERROR"));
    }

    @Test
    void unknownPathReturns404() throws Exception {
        mockMvc.perform(get("/api/ip/8.8.8.8/extra"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void methodNotAllowedReturns405() throws Exception {
        mockMvc.perform(post("/api/ip/8.8.8.8"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"));
        mockMvc.perform(post("/api/ip/8.8.8.8"))
                .andExpect(jsonPath("$.error").value("Method Not Allowed"));
    }

    @Test
    void unexpectedExceptionReturns500() throws Exception {
        when(getIpByIpQuery.execute("8.8.8.8")).thenThrow(new IllegalStateException("boom"));

        mockMvc.perform(get("/api/ip/8.8.8.8"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"));
    }

    @Test
    void successBodyIsIpAddr() throws Exception {
        IpAddr ipAddr = new IpAddr();
        ipAddr.setIp("8.8.8.8");
        when(getIpByIpQuery.execute("8.8.8.8")).thenReturn(Optional.of(ipAddr));

        mockMvc.perform(get("/api/ip/8.8.8.8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ip").value("8.8.8.8"));
    }
}