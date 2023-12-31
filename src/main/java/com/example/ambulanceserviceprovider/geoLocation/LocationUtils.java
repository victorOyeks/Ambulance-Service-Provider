package com.example.ambulanceserviceprovider.geoLocation;

import com.example.ambulanceserviceprovider.dto.request.AmbulanceRequest;
import com.example.ambulanceserviceprovider.dto.request.OrgRegistrationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class LocationUtils {
    private static final String API_KEY = "AIzaSyA22GBhIK3LwSHcYDlB8UYJ4x1IoGeuqvM";
    public static GeoResponse getGeoDetails(@RequestParam OrgRegistrationRequest request) {
        UriComponents uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("maps.googleapis.com")
                .path("/maps/api/geocode/json")
                .queryParam("key", API_KEY)
                .queryParam("address", request.getOrgAddress())
                .build();
        ResponseEntity<GeoResponse> response = new RestTemplate().getForEntity(uri.toUriString(), GeoResponse.class);

        return response.getBody();
    }

    public static GeoResponse getGeoDetails(@RequestParam AmbulanceRequest request) {
        UriComponents uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("maps.googleapis.com")
                .path("/maps/api/geocode/json")
                .queryParam("key", API_KEY)
                .queryParam("address", request.getLocation())
                .build();
        ResponseEntity<GeoResponse> response = new RestTemplate().getForEntity(uri.toUriString(), GeoResponse.class);

        return response.getBody();
    }

    public static String extractActualLocation(GeoResponse geoDetails) {
        if (geoDetails != null && geoDetails.getResult() != null && geoDetails.getResult().length > 0) {
            Result firstResult = geoDetails.getResult()[0];
            if (firstResult.getAddress() != null) {
                return firstResult.getAddress();
            }
        }
        return "Unknown Location";
    }

    public static GeoLocation extractGeoLocation(GeoResponse geoDetails) {
        if (geoDetails != null && geoDetails.getResult() != null && geoDetails.getResult().length > 0) {
            Result firstResult = geoDetails.getResult()[0];
            if (firstResult.getGeometry() != null && firstResult.getGeometry().getLocation() != null) {
                return firstResult.getGeometry().getLocation();
            }
        }
        return null;
    }

    public static String getMapUri(GeoLocation location) {
        UriComponents uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("www.google.com")
                .path("/maps/place/")
                .queryParam("q", location.getLat() + "," + location.getLng())
                .build();

        return uri.toUriString();
    }
}
