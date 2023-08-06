package com.example.ambulanceserviceprovider.service.serviceImp;

import com.example.ambulanceserviceprovider.entities.Organisation;
import com.example.ambulanceserviceprovider.entities.User;
import com.example.ambulanceserviceprovider.exceptions.CustomException;
import com.example.ambulanceserviceprovider.repository.OrganisationRepository;
import com.example.ambulanceserviceprovider.repository.UserRepository;
import com.example.ambulanceserviceprovider.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;

    @Override
    public String verifyAccount(String verificationToken) {
        User user = userRepository.findByVerificationToken(verificationToken);
        Organisation organisation = organisationRepository.findByVerificationToken(verificationToken);
        if (user != null) {
            user.setEnabled(true);
            user.setVerificationToken(null);
            userRepository.save(user);
            return "Account verified successfully. Proceed to login.";
        } else if (organisation != null) {
            organisation.setEnabled(true);
            organisation.setVerificationToken(null);
            return "Account verified successfully. Proceed to login.";
        }
        throw new CustomException("Invalid verification token or email.");
    }
}
