package com.example.ambulanceserviceprovider.dto.request;

import com.example.ambulanceserviceprovider.constant.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDto {
    private String fullName;
    private UserType userType;
}
