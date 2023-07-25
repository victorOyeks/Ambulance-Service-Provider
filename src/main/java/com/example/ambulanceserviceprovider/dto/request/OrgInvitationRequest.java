package com.example.ambulanceserviceprovider.dto.request;

import com.example.ambulanceserviceprovider.constant.OrganisationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrgInvitationRequest {
    private String email;
    private String note;
    private OrganisationType organisationType;
}
