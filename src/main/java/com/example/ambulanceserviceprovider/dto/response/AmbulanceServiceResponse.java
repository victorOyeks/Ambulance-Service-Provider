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
    private String driverName;
    private String doctorName;
    private String attendeeName;
    private String employeeName;
    private String location;
    private String mapUri;
    private String message;
}
