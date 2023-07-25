package com.example.ambulanceserviceprovider.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserType {

    ADMIN("ROLE_ADMIN"),
    INDIVIDUAL("ROLE_INDIVIDUAL"),
    TRAFFIC_PATROL("ROLE_TRAFFIC_PATROL"),
    DOCTOR("ROLE_DOCTOR"),
    EMPLOYEE("ROLE_EMPLOYEE"),
    ATTENDEE("ROLE_ATTENDEE"),
    DRIVER("ROLE_DRIVER");

    private final String name;
}