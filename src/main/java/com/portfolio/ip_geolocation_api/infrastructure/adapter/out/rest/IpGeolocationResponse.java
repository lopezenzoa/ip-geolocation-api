package com.portfolio.ip_geolocation_api.infrastructure.adapter.out.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class IpGeolocationResponse {

    private String ip;
    private String continentCode;
    private String continentName;
    private String countryCode2;
    private String countryCode3;
    private String countryName;
    private String countryNameOfficial;
    private String countryCapital;
    private String stateProv;
    private String stateCode;
    private String district;
    private String city;
    private String zipcode;
    private String latitude;
    private String longitude;
    private Boolean isEu;
    private String countryFlag;
    private String geonameId;
    private String countryEmoji;
}