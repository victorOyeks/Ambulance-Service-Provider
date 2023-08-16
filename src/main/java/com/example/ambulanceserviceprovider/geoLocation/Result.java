package com.example.ambulanceserviceprovider.geoLocation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Result {
    @JsonProperty("formatted_address")
    private String address;
    @JsonProperty("geometry")
    private Geometry geometry;
}
