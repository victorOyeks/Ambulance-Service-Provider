package com.example.ambulanceserviceprovider.dto.request;

import com.example.ambulanceserviceprovider.constant.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SignupRequest {
    private String userEmail;
    private String firstName;
    private String lastName;
    private String password;
    private String confirmPassword;
    private UserType userType;
}
