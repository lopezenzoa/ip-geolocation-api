package com.portfolio.ip_geolocation_api.infrastructure.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.portfolio.ip_geolocation_api.domain.exception.IpAddrAlreadyExistsException;
import com.portfolio.ip_geolocation_api.domain.exception.IpAddrNotFoundException;
import com.portfolio.ip_geolocation_api.domain.exception.InvalidIpAddrDataException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IpAddrNotFoundException.class)
    public ResponseEntity<String> handleNotFound(IpAddrNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(IpAddrAlreadyExistsException.class)
    public ResponseEntity<String> handleAlreadyExists(IpAddrAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(InvalidIpAddrDataException.class)
    public ResponseEntity<String> handleInvalidData(InvalidIpAddrDataException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}