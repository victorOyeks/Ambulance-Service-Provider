package com.example.ambulanceserviceprovider.service.serviceImp;

import com.example.ambulanceserviceprovider.constant.UserType;
import com.example.ambulanceserviceprovider.dto.request.*;
import com.example.ambulanceserviceprovider.dto.response.OrgRegistrationResponse;
import com.example.ambulanceserviceprovider.dto.response.LoginResponse;
import com.example.ambulanceserviceprovider.dto.response.SignupResponse;
import com.example.ambulanceserviceprovider.entities.Notification;
import com.example.ambulanceserviceprovider.entities.Organisation;
import com.example.ambulanceserviceprovider.entities.User;
import com.example.ambulanceserviceprovider.exceptions.CustomException;
import com.example.ambulanceserviceprovider.geoLocation.GeoLocation;
import com.example.ambulanceserviceprovider.geoLocation.GeoResponse;
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
    private final EmailService emailService;

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