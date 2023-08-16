package com.example.ambulanceserviceprovider.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrgRegistrationRequest {
    private String email;
    private String orgName;
    private String orgAddress;
    private String password;
    private String confirmPassword;
    private String phoneNumber;
}
