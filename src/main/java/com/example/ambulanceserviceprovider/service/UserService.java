package com.example.ambulanceserviceprovider.service;

import com.example.ambulanceserviceprovider.dto.request.*;
import com.example.ambulanceserviceprovider.dto.response.LoginResponse;
import com.example.ambulanceserviceprovider.dto.response.SignupResponse;

import java.io.IOException;
import java.util.List;

public interface UserService {
    SignupResponse signup(SignupRequest signupRequest);
    LoginResponse authenticate(LoginRequest loginRequest);
    List<UserDto> searchUsers(String name);
    String changePassword(ChangePasswordRequest request);
    String forgotPassword(ResetEmail resetEmail) throws IOException;
    String resetPassword(ResetPasswordRequest resetPasswordRequest);
}
