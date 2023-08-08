package com.example.ambulanceserviceprovider.controller;

import com.example.ambulanceserviceprovider.dto.request.ChangePasswordRequest;
import com.example.ambulanceserviceprovider.dto.response.AmbulanceServiceResponse;
import com.example.ambulanceserviceprovider.dto.response.ApiResponse;
import com.example.ambulanceserviceprovider.service.AmbulanceService;
import com.example.ambulanceserviceprovider.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/ambulance")
@RequiredArgsConstructor
public class AmbulanceController {

    private final AmbulanceService ambulanceService;
    private final UserService userService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<AmbulanceServiceResponse>> requestAmbulanceService() {
        ApiResponse<AmbulanceServiceResponse> apiResponse = new ApiResponse<>(ambulanceService.requestAmbulanceService());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/revert")
    public ResponseEntity<ApiResponse<String>> revertAmbulanceService() {
        ApiResponse<String > apiResponse = new ApiResponse<>(ambulanceService.revertUnavailableEntities());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        ApiResponse<String> apiResponse = new ApiResponse<>(userService.changePassword(changePasswordRequest));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}

