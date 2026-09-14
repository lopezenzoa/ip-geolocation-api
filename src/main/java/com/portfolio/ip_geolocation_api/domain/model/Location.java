package com.portfolio.ip_geolocation_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Location {

    private String continentCode;
    private String continentName;
    private String countryCode;
    private String countryName;
    private String countryCapital;
    private String stateProv;
    private String district;
    private String city;
    private String latitude;
    private String longitude;
    private String countryFlag;
}
