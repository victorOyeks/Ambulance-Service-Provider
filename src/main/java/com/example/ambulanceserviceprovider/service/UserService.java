package com.example.ambulanceserviceprovider.service;

import com.example.ambulanceserviceprovider.dto.request.LoginRequest;
import com.example.ambulanceserviceprovider.dto.request.SignupRequest;
import com.example.ambulanceserviceprovider.dto.request.UserDto;
import com.example.ambulanceserviceprovider.dto.response.LoginResponse;
import com.example.ambulanceserviceprovider.dto.response.SignupResponse;

import java.util.List;

public interface UserService {
    SignupResponse signup(SignupRequest signupRequest);
    LoginResponse authenticate(LoginRequest loginRequest);
    List<UserDto> searchUsers(String name);
}
