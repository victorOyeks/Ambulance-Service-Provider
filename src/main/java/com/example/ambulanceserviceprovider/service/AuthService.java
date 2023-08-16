package com.example.ambulanceserviceprovider.service;

import com.example.ambulanceserviceprovider.dto.request.LoginRequest;
import com.example.ambulanceserviceprovider.dto.request.OrgRegistrationRequest;
import com.example.ambulanceserviceprovider.dto.request.SignupRequest;
import com.example.ambulanceserviceprovider.dto.response.LoginResponse;
import com.example.ambulanceserviceprovider.dto.response.OrgRegistrationResponse;
import com.example.ambulanceserviceprovider.dto.response.SignupResponse;

public interface AuthService {
    SignupResponse userSignup(SignupRequest signupRequest);
    OrgRegistrationResponse organizationSignup (OrgRegistrationRequest request);
    LoginResponse authenticate(LoginRequest loginRequest);
    String verifyAccount(String verificationToken);
}
