package com.example.ambulanceserviceprovider.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrganisationType {
    HOSPITAL ("ROLE_HOSPITAL"),
    PRIMARY_HEALTH_CARE("ROLE_PRIMARY_HEALTH_CARE");

    private final String name;
}
