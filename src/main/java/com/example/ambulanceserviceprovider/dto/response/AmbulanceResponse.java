package com.example.ambulanceserviceprovider.dto.response;

import com.example.ambulanceserviceprovider.constant.AvailabilityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AmbulanceResponse {
    private String platNumber;
    private AvailabilityStatus availabilityStatus;
}
