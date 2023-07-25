package com.example.ambulanceserviceprovider.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AmbulanceServiceResponse {
    private String ambulancePlateNumber;
    private String driverNames;
    private String doctorNames;
    private String attendeeNames;
    private String employeeNames;
    private String message;
}
