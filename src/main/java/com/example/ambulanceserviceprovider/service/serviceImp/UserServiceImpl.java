package com.example.ambulanceserviceprovider.service.serviceImp;

import com.example.ambulanceserviceprovider.constant.UserType;
import com.example.ambulanceserviceprovider.dto.request.*;
import com.example.ambulanceserviceprovider.dto.response.LoginResponse;
import com.example.ambulanceserviceprovider.dto.response.SignupResponse;
import com.example.ambulanceserviceprovider.entities.Notification;
import com.example.ambulanceserviceprovider.entities.Organisation;
import com.example.ambulanceserviceprovider.entities.User;
import com.example.ambulanceserviceprovider.exceptions.CustomException;
import com.example.ambulanceserviceprovider.repository.OrganisationRepository;
import com.example.ambulanceserviceprovider.repository.UserRepository;
import com.example.ambulanceserviceprovider.security.JwtService;
import com.example.ambulanceserviceprovider.service.EmailService;
import com.example.ambulanceserviceprovider.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public SignupResponse signup(SignupRequest signupRequest) {
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

    public List<UserDto> searchUsers(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new CustomException("Name term parameter is required.");
        }
        List<User> users = userRepository.findByFirstNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(name, name);
        if (users.isEmpty()) {
            throw new CustomException("No users found matching the search criteria.");
        }
        // Convert User objects to UserDTO objects containing full name and userType
        return users.stream()
                .map(user -> new UserDto(getFullName(user), user.getUserType()))
                .collect(Collectors.toList());
    }

    public String changePassword(ChangePasswordRequest request) {
        User existingUser = getAuthenticatedUser();

        if (!passwordEncoder.matches(request.getOldPassword(), existingUser.getPassword())) {
            throw new CustomException("Incorrect old password");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            System.out.println("New password: " + request.getNewPassword());
            System.out.println("Confirm password: " + request.getConfirmNewPassword());
            throw new CustomException("New password and confirm password do not match");
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        existingUser.setPassword(encodedPassword);

        userRepository.save(existingUser);

        return "Password changed successfully!!!";
    }


    @Override
    public String forgotPassword(ResetEmail resetEmail) throws IOException {
        String email = resetEmail.getEmail();

        User user = userRepository.findByEmail(email);
        Organisation organisation = organisationRepository.findByEmail(email);

        if (user == null && organisation == null) {
            throw new CustomException("User with " + email + " does not exist");
        }

        String resetToken = generateResetToken();

        if (user != null) {
            user.setToken(resetToken);
            userRepository.save(user);
        } else {
            organisation.setVerificationToken(resetToken);
            organisationRepository.save(organisation);
        }
        sendPasswordResetEmail(email, resetToken);

        return "Password reset code has been sent to your email address!!!.";
    }

    public String resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String resetToken = resetPasswordRequest.getResetToken();
        String newPassword = resetPasswordRequest.getNewPassword();
        String confirmNewPassword = resetPasswordRequest.getConfirmPassword();

        User user = userRepository.findByToken(resetToken);
        Organisation organisation = organisationRepository.findByVerificationToken(resetToken);

        if (user != null) {
            if(!newPassword.equals(confirmNewPassword)) {
                throw new CustomException("Password does not match");
            }
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            user.setToken(null);
            userRepository.save(user);
            return "User password reset successful.";
        } else if (organisation != null) {
            if(!newPassword.equals(confirmNewPassword)) {
                throw new CustomException("Password does not match");
            }
            String encodedPassword = passwordEncoder.encode(newPassword);
            organisation.setPassword(encodedPassword);
            organisation.setVerificationToken(null);
            organisationRepository.save(organisation);
            return "Organisation password reset successful.";
        }
        else {
            throw new CustomException("Invalid password reset token.");
        }
    }

    private String generateResetToken() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000000);
        return String.format("%06d", randomNumber);
    }


    private void sendPasswordResetEmail(String recipient, String resetCode) throws IOException {
        String subject = "Password Reset";
        String messageBody = "Your password reset code is :" + resetCode;
        EmailDetails emailDetails = new EmailDetails(recipient, subject, messageBody);
        emailService.sendEmail(emailDetails);
    }

    private String getFullName(User user) {
            return user.getFirstName() + " " + user.getLastName();
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

    private boolean isRestrictedUserType(UserType userType) {
        UserType[] restrictedUserTypes = {UserType.ADMIN, UserType.DOCTOR, UserType.EMPLOYEE, UserType.ATTENDEE};
        for (UserType restrictedType : restrictedUserTypes) {
            if (userType == restrictedType) {
                return true;
            }
        }
        return false;
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

    private void createNotification(User user, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        user.getNotifications().add(notification);
        userRepository.save(user);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new CustomException("User not found");
        }
        return user;
    }
}