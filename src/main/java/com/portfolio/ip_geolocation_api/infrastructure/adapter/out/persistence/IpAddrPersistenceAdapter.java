package com.portfolio.ip_geolocation_api.infrastructure.adapter.out.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import com.portfolio.ip_geolocation_api.application.port.out.IpAddrPersistencePort;
import com.portfolio.ip_geolocation_api.domain.model.IpAddr;

@Component
public class IpAddrPersistenceAdapter implements IpAddrPersistencePort {

    private final ObjectMapper objectMapper;
    private final Path cacheFile;

    public IpAddrPersistenceAdapter(
            ObjectMapper objectMapper,
            @Value("${geolocation.cache.file}") String cacheFilePath) {
        this.objectMapper = objectMapper;
        this.cacheFile = Path.of(cacheFilePath);
    }

    @Override
    public synchronized Optional<IpAddr> findByIp(String ip) {
        return loadAll().stream()
                .filter(ipAddr -> ipAddr.getIp().equals(ip))
                .findFirst();
    }

    @Override
    public synchronized List<IpAddr> findAll() {
        return List.copyOf(loadAll());
    }

    @Override
    public synchronized void save(IpAddr ipAddr) {
        List<IpAddr> all = loadAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getIp().equals(ipAddr.getIp())) {
                all.set(i, ipAddr);
                writeAll(all);
                return;
            }
        }
        all.add(ipAddr);
        writeAll(all);
    }

    @Override
    public synchronized void delete(String ip) {
        List<IpAddr> all = loadAll();
        all.removeIf(ipAddr -> ipAddr.getIp().equals(ip));
        writeAll(all);
    }

    private List<IpAddr> loadAll() {
        if (!Files.exists(cacheFile)) {
            return new ArrayList<>();
        }
        try {
            return new ArrayList<>(objectMapper.readValue(cacheFile.toFile(),
                    new TypeReference<List<IpAddr>>() { }));
        } catch (JacksonException e) {
            throw new IpCachePersistenceException("Failed to parse IP cache file: " + cacheFile, e);
        }
    }

    private void writeAll(List<IpAddr> ipAddrs) {
        try {
            if (cacheFile.getParent() != null) {
                Files.createDirectories(cacheFile.getParent());
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(cacheFile.toFile(), ipAddrs);
        } catch (IOException e) {
            throw new IpCachePersistenceException("Failed to write IP cache file: " + cacheFile, e);
        }
    }
}