package com.example.ambulanceserviceprovider.service.serviceImp;

import com.example.ambulanceserviceprovider.constant.UserType;
import com.example.ambulanceserviceprovider.dto.request.EmailDetails;
import com.example.ambulanceserviceprovider.dto.request.LoginRequest;
import com.example.ambulanceserviceprovider.dto.request.OrgRegistrationRequest;
import com.example.ambulanceserviceprovider.dto.request.SignupRequest;
import com.example.ambulanceserviceprovider.dto.response.LoginResponse;
import com.example.ambulanceserviceprovider.dto.response.OrgRegistrationResponse;
import com.example.ambulanceserviceprovider.dto.response.SignupResponse;
import com.example.ambulanceserviceprovider.entities.Notification;
import com.example.ambulanceserviceprovider.entities.Organisation;
import com.example.ambulanceserviceprovider.entities.User;
import com.example.ambulanceserviceprovider.exceptions.CustomException;
import com.example.ambulanceserviceprovider.geoLocation.GeoLocation;
import com.example.ambulanceserviceprovider.geoLocation.GeoResponse;
import com.example.ambulanceserviceprovider.geoLocation.LocationUtils;
import com.example.ambulanceserviceprovider.repository.OrganisationRepository;
import com.example.ambulanceserviceprovider.repository.UserRepository;
import com.example.ambulanceserviceprovider.security.JwtService;
import com.example.ambulanceserviceprovider.service.AuthService;
import com.example.ambulanceserviceprovider.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.ambulanceserviceprovider.geoLocation.LocationUtils.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;

    public SignupResponse userSignup(SignupRequest signupRequest) {
        String userEmail = signupRequest.getUserEmail();

        if (!isValidEmail(userEmail)) {
            throw new CustomException("Invalid email format");
        }

        if (organisationRepository.existsByEmail(userEmail)){
            throw new CustomException("Organisation with " + userEmail + " already exist");
        }

        if (!isPasswordValid(signupRequest.getPassword())) {
            throw new CustomException("Password must contain at least 6 characters, a number, a character and a capital letter!!!");
        }

        if (userRepository.existsByEmail(userEmail)) {
            User existingUser = userRepository.findByEmail(userEmail);

            if (existingUser.getPassword() != null) {
                throw new CustomException("User with " + userEmail + " already exist.");
            }
            String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
            existingUser.setPassword(encodedPassword);
            existingUser.setFirstName(signupRequest.getFirstName());
            existingUser.setLastName(signupRequest.getLastName());
//            existingUser.setUserType(existingUser.getUserType());
            existingUser.setEnabled(true);
            existingUser.setLocked(false);

            userRepository.save(existingUser);

            return SignupResponse.builder()
                    .userId(existingUser.getUserId())
                    .email(existingUser.getEmail())
                    .userType(existingUser.getUserType())
                    .build();
        } else {

            if (isRestrictedUserType(signupRequest.getUserType())) {
                throw new CustomException("Restricted UserType. UserType can only be signed up by the Admin");
            }

            if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
                throw new CustomException("Password does not match");
            }

            String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

            String signupToken = generateSignupToken();

            User user = new User();
            user.setEmail(signupRequest.getUserEmail());
            user.setFirstName(signupRequest.getFirstName());
            user.setLastName(signupRequest.getLastName());
            user.setPassword(encodedPassword);
            user.setUserType(signupRequest.getUserType());
            user.setToken(signupToken);
            user.setEnabled(false);
            user.setLocked(false);

            createNotification(user, "Hello " + user.getFirstName() + "! Welcome onboard!");
            userRepository.save(user);


            String invitationLink = "http://localhost:9000/api/auth/verify?token=" + signupToken;
            String subject = "Account Validation!!!";
            String messageBody = "Dear User,\n\nThank you for signing up on the platform. Please click the link below to complete your registration:\n\n" + invitationLink;
            EmailDetails emailDetails = new EmailDetails(signupRequest.getUserEmail(), subject, messageBody);

            emailService.sendEmail(emailDetails);


            return SignupResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .userType(user.getUserType())
                    .message("Sign up was successful. Proceed to your email for verification!!!")
                    .build();
        }
    }

    @Override
    public OrgRegistrationResponse organizationSignup (OrgRegistrationRequest request) {

        GeoResponse geoDetails = getGeoDetails(request);
        String actualLocation = extractActualLocation(geoDetails);
        GeoLocation coordinates = extractGeoLocation(geoDetails);

        String email = request.getEmail();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        boolean isExistingOrg = organisationRepository.existsByEmail(email);
        if (!isExistingOrg) {
            throw new CustomException("Invalid Organization. Contact Admin!");
        }

        boolean isExistingUser = userRepository.existsByEmail(email);
        if(isExistingUser) {
            throw new CustomException("User with " + email + " already exist!");
        }

        if (!password.equals(confirmPassword)) {
            throw new CustomException("Password does not match");
        }

        Organisation existingOrg = organisationRepository.findByEmail(email);

        if (!existingOrg.getEnabled()) {
            throw new CustomException("Invalid Organization. Contact Admin");
        }
        if(existingOrg.getPassword() != null) {
            throw new CustomException("Organization already exist with email");
        }

//        String verificationToken = generateToken();
        String encodedPassword = passwordEncoder.encode(password);

        String mapUri = LocationUtils.getMapUri(coordinates);

        existingOrg.setOrgName(request.getOrgName());
        existingOrg.setPassword(encodedPassword);
        existingOrg.setPhoneNumber(request.getPhoneNumber());
        existingOrg.setOrgAddress(actualLocation);
        existingOrg.setCoordinates(coordinates);
        existingOrg.setMapUrl(mapUri);

        organisationRepository.save(existingOrg);

//        sendVerificationEmail(request.getEmail(), verificationToken);

        return OrgRegistrationResponse.builder()
                .orgId(existingOrg.getOrgId())
                .email(existingOrg.getEmail())
                .orgName(existingOrg.getOrgName())
                .orgAddress(existingOrg.getOrgAddress())
                .mapUri(existingOrg.getMapUrl())
                .organisationType(existingOrg.getOrganisationType())
                .build();
    }

    private String generateSignupToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public LoginResponse authenticate(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        User user = userRepository.findByEmail(email);
        Organisation organisation = organisationRepository.findByEmail(email);

        if (user == null && organisation == null) {
            throw new CustomException ("User with " + email + " does not exist");
        }
        if (user != null) {
            if (!user.getEnabled()) {
                throw new CustomException("Your email has not been verified!!!");
            }
            if(user.getLocked()) {
                throw new CustomException("Your account has been locked. Contact Admin for support!");
            }
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return performLogin(loginRequest, user.getUserType());
            }

        } else {
            if(!organisation.getEnabled()) {
                throw new CustomException("Your account has not been enabled");
            }
            if(organisation.getLocked()) {
                throw new CustomException("Your account has been Locked. Contact Admin for support!");
            }
            if (passwordEncoder.matches(loginRequest.getPassword(), organisation.getPassword())) {
                return performLogin(loginRequest, organisation.getOrganisationType());
            }
        }
        throw new CustomException("Invalid username or password");
    }


    @Override
    public String verifyAccount(String verificationToken) {
        User user = userRepository.findByToken(verificationToken);
        Organisation organisation = organisationRepository.findByVerificationToken(verificationToken);
        if (user != null) {
            user.setEnabled(true);
            user.setToken(null);
            userRepository.save(user);
            return "Account verified successfully. Proceed to login.";
        } else if (organisation != null) {
            organisation.setEnabled(true);
            organisation.setVerificationToken(null);
            return "Account verified successfully. Proceed to login.";
        }
        throw new CustomException("Invalid verification token or email.");
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 6) {
            return false;
        }
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();

    }

    private boolean isRestrictedUserType(UserType userType) {
        UserType[] restrictedUserTypes = {UserType.ADMIN, UserType.DOCTOR, UserType.EMPLOYEE, UserType.ATTENDEE};
        for (UserType restrictedType : restrictedUserTypes) {
            if (userType == restrictedType) {
                return true;
            }
        }
        return false;
    }

    private void createNotification(User user, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        user.getNotifications().add(notification);
        userRepository.save(user);
    }

    private LoginResponse performLogin(LoginRequest loginRequest, Enum<?> userType) {
        String email = loginRequest.getEmail();
        String accessToken = jwtService.generateToken(createAuthentication(email, loginRequest.getPassword()));
        String refreshToken = jwtService.generateRefreshToken(createAuthentication(email, loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(createAuthentication(email, loginRequest.getPassword()));

        return LoginResponse.builder()
                .email(email)
                .message(userType + " logged in successfully!!!")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private Authentication createAuthentication(String username, String password) {
        return new UsernamePasswordAuthenticationToken(username, password);
    }
}
