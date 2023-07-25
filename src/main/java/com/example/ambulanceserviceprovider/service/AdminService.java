package com.example.ambulanceserviceprovider.service;

import com.example.ambulanceserviceprovider.dto.request.OrgInvitationRequest;
import com.example.ambulanceserviceprovider.dto.request.UserInvitationRequest;
import com.example.ambulanceserviceprovider.dto.response.AmbulanceResponse;
import com.example.ambulanceserviceprovider.entities.Ambulance;

public interface AdminService {

    String invite(UserInvitationRequest invitationRequest);
    String invite(OrgInvitationRequest invitationRequest);
    AmbulanceResponse addAmbulance ();

//    String inviteStaff(UserInvitationRequest userInvitationRequest);
//    String inviteOrg(OrgInvitationRequest orgInvitationRequest);
}
