package com.example.ambulanceserviceprovider.service.serviceImp;

import com.example.ambulanceserviceprovider.constant.AvailabilityStatus;
import com.example.ambulanceserviceprovider.constant.OrganisationType;
import com.example.ambulanceserviceprovider.constant.UserType;
import com.example.ambulanceserviceprovider.dto.request.EmailDetails;
import com.example.ambulanceserviceprovider.dto.request.OrgInvitationRequest;
import com.example.ambulanceserviceprovider.dto.request.UserInvitationRequest;
import com.example.ambulanceserviceprovider.dto.response.AmbulanceResponse;
import com.example.ambulanceserviceprovider.entities.Ambulance;
import com.example.ambulanceserviceprovider.entities.Organisation;
import com.example.ambulanceserviceprovider.entities.User;
import com.example.ambulanceserviceprovider.exceptions.CustomException;
import com.example.ambulanceserviceprovider.repository.AmbulanceRepository;
import com.example.ambulanceserviceprovider.repository.OrganisationRepository;
import com.example.ambulanceserviceprovider.repository.UserRepository;
import com.example.ambulanceserviceprovider.service.AdminService;
import com.example.ambulanceserviceprovider.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImp implements AdminService {

    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;
    private final EmailService emailService;
    private final AmbulanceRepository ambulanceRepository;

    public String invite(UserInvitationRequest invitationRequest) {
        User user = getAuthenticatedUser();
        if(!user.getUserType().equals(UserType.ADMIN)){
            throw new CustomException("You are not authorized to invite");
        }
        String email = invitationRequest.getEmail();
        String note = invitationRequest.getNote();
        boolean isForOrganization = false;

        return performInvitation(email, note, invitationRequest.getUserType(), isForOrganization);
    }

    public String invite(OrgInvitationRequest invitationRequest) {
        User user = getAuthenticatedUser();
        if(!user.getUserType().equals(UserType.ADMIN)){
            throw new CustomException("You are not authorized to invite");
        }
        String email = invitationRequest.getEmail();
        String note = invitationRequest.getNote();
        boolean isForOrganization = true;

        return performInvitation(email, note, invitationRequest.getOrganisationType(), isForOrganization);
    }

    private String performInvitation(String email, String note, Enum<?> type, boolean isForOrganization) {
        boolean existingUser = userRepository.existsByEmail(email);
        boolean existingOrg = organisationRepository.existsByEmail(email);

        if (existingUser || existingOrg) {
            throw new CustomException("User with " + email + " already exists");
        }

        if (!isAllowedType(type, isForOrganization)) {
            throw new CustomException("Invalid type. Only " + getAllowedTypes(isForOrganization) + " are allowed to be invited");
        }

        if (!isForOrganization) {
            User user = new User();
            user.setEmail(email);
            user.setEnabled(false);
            user.setUserType((UserType) type);
            user.setLocked(false);
            user.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            userRepository.save(user);
        } else {
            Organisation organisation = new Organisation();
            organisation.setEmail(email);
            organisation.setLocked(false);
            organisation.setEnabled(true);
            organisation.setOrganisationType((OrganisationType) type);
            organisationRepository.save(organisation);
        }

        String signupToken = generateSignupToken();

        String invitationLink = "http://localhost:9191/api/auth/verify?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8) + "&token=" + signupToken;
        String subject = "Invitation to Sign Up";
        String messageBody = "Dear User,\n\nYou have been invited to sign up on our platform. Please click the link below to complete your registration:\n\n" + invitationLink + "\n\nNote from the admin: " + note;
        EmailDetails emailDetails = new EmailDetails(email, subject, messageBody);

        emailService.sendEmail(emailDetails);

        return type.toString() + " onboarded successfully. Email sent to " + type.toString() + " to complete registration";
    }

    public AmbulanceResponse addAmbulance() {
        Ambulance ambulance = new Ambulance();
        ambulance.setPlateNumber(generatePlateNumber());
        ambulance.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);

        boolean existingAmbulance = ambulanceRepository.existsByPlateNumber(ambulance.getPlateNumber());
        if(existingAmbulance) {
            throw new CustomException("Ambulance with the plate number " + ambulance.getPlateNumber() + " already exist!!!");
        }

        ambulanceRepository.save(ambulance);
        return AmbulanceResponse.builder()
                .platNumber(ambulance.getPlateNumber())
                .availabilityStatus(ambulance.getAvailabilityStatus())
                .build();
    }


    private boolean isAllowedType(Enum<?> type, boolean isForOrganization) {
        if (isForOrganization) {
            return type == OrganisationType.HOSPITAL || type == OrganisationType.PRIMARY_HEALTH_CARE;
        } else {
            return type == UserType.DOCTOR || type == UserType.EMPLOYEE || type == UserType.ATTENDEE || type == UserType.DRIVER;
        }
    }

    private String getAllowedTypes(boolean isForOrganization) {
        if (isForOrganization) {
            return "HOSPITAL, PRIMARY_HEALTH_CARE";
        } else {
            return "DOCTOR, EMPLOYEE, ATTENDEE, DRIVER";
        }
    }

    private String generateSignupToken() {
        UUID signupToken = UUID.randomUUID();
        return signupToken.toString();
    }

    public String generatePlateNumber() {
        StringBuilder figureBuilder = new StringBuilder();

        // Generate first three capital letters
        for (int i = 0; i < 3; i++) {
            char capitalLetter = (char) (new Random().nextInt(26) + 'A');
            figureBuilder.append(capitalLetter);
        }

        // Add hyphen
        figureBuilder.append("-");

        // Generate two random numbers
        for (int i = 0; i < 2; i++) {
            int randomNumber = new Random().nextInt(10);
            figureBuilder.append(randomNumber);
        }

        // Add hyphen
        figureBuilder.append("-");

        // Generate last two capital letters
        for (int i = 0; i < 2; i++) {
            char capitalLetter = (char) (new Random().nextInt(26) + 'A');
            figureBuilder.append(capitalLetter);
        }

        return figureBuilder.toString();
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new CustomException("User not found");
        }
        return user;
    }
}