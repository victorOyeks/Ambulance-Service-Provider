package com.example.ambulanceserviceprovider.service;

import com.example.ambulanceserviceprovider.constant.UserType;
import com.example.ambulanceserviceprovider.dto.response.AmbulanceServiceResponse;

public interface AmbulanceService {

    AmbulanceServiceResponse requestAmbulanceService();
    String revertUnavailableEntities();
}
