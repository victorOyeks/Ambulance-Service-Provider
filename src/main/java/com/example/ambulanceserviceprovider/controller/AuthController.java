package com.example.ambulanceserviceprovider.controller;

import com.example.ambulanceserviceprovider.dto.request.*;
import com.example.ambulanceserviceprovider.dto.response.ApiResponse;
import com.example.ambulanceserviceprovider.dto.response.LoginResponse;
import com.example.ambulanceserviceprovider.dto.response.SignupResponse;
import com.example.ambulanceserviceprovider.exceptions.CustomException;
import com.example.ambulanceserviceprovider.service.AuthService;
import com.example.ambulanceserviceprovider.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("sign-up")
    public ResponseEntity<ApiResponse<SignupResponse>> signup (@RequestBody SignupRequest signupRequest) {
        ApiResponse<SignupResponse> apiResponse = new ApiResponse<>(userService.signup(signupRequest));
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping("verify")
    public ResponseEntity<ApiResponse<String>> verifyAccount (@RequestParam("token") String token) {
        ApiResponse<String> apiResponse = new ApiResponse<>(authService.verifyAccount(token));
        return  new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("authenticate")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@RequestBody LoginRequest loginRequest) {
        ApiResponse<LoginResponse> apiResponse = new ApiResponse<>(userService.authenticate(loginRequest));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam(required = false) String name) {
        List<UserDto> users = userService.searchUsers(name);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody ResetEmail resetEmail) {
        ApiResponse<String> apiResponse;
        try {
            String forgotPasswordResponse = userService.forgotPassword(resetEmail);
            apiResponse = new ApiResponse<>(forgotPasswordResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (CustomException exception) {
            apiResponse = new ApiResponse<>(exception.getMessage());
            return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        ApiResponse<String> apiResponse;

        try {
            String resetPasswordResponse = userService.resetPassword(resetPasswordRequest);
            apiResponse = new ApiResponse<>(resetPasswordResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (CustomException exception) {
            apiResponse = new ApiResponse<>(exception.getMessage());
            return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
        }
    }
}
