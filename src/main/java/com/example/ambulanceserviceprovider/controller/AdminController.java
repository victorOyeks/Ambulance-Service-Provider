package com.example.ambulanceserviceprovider.controller;

import com.example.ambulanceserviceprovider.dto.request.OrgInvitationRequest;
import com.example.ambulanceserviceprovider.dto.request.UserInvitationRequest;
import com.example.ambulanceserviceprovider.dto.response.AmbulanceResponse;
import com.example.ambulanceserviceprovider.dto.response.ApiResponse;
import com.example.ambulanceserviceprovider.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("invite-staff")
    public ResponseEntity<ApiResponse<String>> inviteStaff (@RequestBody UserInvitationRequest invitationRequest) {
        ApiResponse<String> apiResponse = new ApiResponse<>(adminService.invite(invitationRequest));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("invite-org")
    public ResponseEntity<ApiResponse<String>> inviteOrg (@RequestBody OrgInvitationRequest invitationRequest) {
        ApiResponse<String> apiResponse = new ApiResponse<>(adminService.invite(invitationRequest));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("add-ambulance")
    public ResponseEntity<ApiResponse<AmbulanceResponse>> addAmbulance() {
        ApiResponse<AmbulanceResponse> apiResponse = new ApiResponse<>(adminService.addAmbulance());
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }
}
