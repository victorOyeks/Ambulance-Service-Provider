package com.example.ambulanceserviceprovider.dto.response;

import com.example.ambulanceserviceprovider.constant.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SignupResponse {
    private Long userId;
    private String email;
    private UserType userType;
}
