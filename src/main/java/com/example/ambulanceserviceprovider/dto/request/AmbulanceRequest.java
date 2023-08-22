package com.example.ambulanceserviceprovider.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AmbulanceRequest {
    private String location;
    private String note;
}
