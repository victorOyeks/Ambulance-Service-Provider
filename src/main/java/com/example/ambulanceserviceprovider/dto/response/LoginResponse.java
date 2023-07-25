package com.example.ambulanceserviceprovider.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LoginResponse {
    private String email;
    private String accessToken;
    private String refreshToken;
    private String message;
}
