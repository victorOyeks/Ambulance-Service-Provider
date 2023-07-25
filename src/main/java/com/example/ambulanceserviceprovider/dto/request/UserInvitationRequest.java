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
public class UserInvitationRequest {
    private String email;
    private String note;
    private UserType userType;
}
