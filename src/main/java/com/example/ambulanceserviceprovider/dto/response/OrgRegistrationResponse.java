package com.example.ambulanceserviceprovider.dto.response;

import com.example.ambulanceserviceprovider.constant.OrganisationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrgRegistrationResponse {
    private Long orgId;
    private String email;
    private String orgName;
    private String orgAddress;
    private String mapUri;
    private OrganisationType organisationType;
}
