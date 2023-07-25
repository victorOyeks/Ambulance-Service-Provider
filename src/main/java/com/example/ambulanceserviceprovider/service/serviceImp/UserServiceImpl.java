package com.example.ambulanceserviceprovider.service.serviceImp;

import com.example.ambulanceserviceprovider.constant.UserType;
import com.example.ambulanceserviceprovider.dto.request.LoginRequest;
import com.example.ambulanceserviceprovider.dto.request.SignupRequest;
import com.example.ambulanceserviceprovider.dto.request.UserDto;
import com.example.ambulanceserviceprovider.dto.response.LoginResponse;
import com.example.ambulanceserviceprovider.dto.response.SignupResponse;
import com.example.ambulanceserviceprovider.entities.Notification;
import com.example.ambulanceserviceprovider.entities.Organisation;
import com.example.ambulanceserviceprovider.entities.User;
import com.example.ambulanceserviceprovider.exceptions.CustomException;
import com.example.ambulanceserviceprovider.repository.OrganisationRepository;
import com.example.ambulanceserviceprovider.repository.UserRepository;
import com.example.ambulanceserviceprovider.security.JwtService;
import com.example.ambulanceserviceprovider.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
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

    public SignupResponse signup(SignupRequest signupRequest) {
        String userEmail = signupRequest.getUserEmail();

        if (!isValidEmail(userEmail)) {
            throw new CustomException("Invalid email format");
        }

        if (organisationRepository.existsByOrgEmail(userEmail)){
            throw new CustomException("Organisation with " + userEmail + " already exist");
        }

        if (userRepository.existsByUserEmail(userEmail)) {
            User existingUser = userRepository.findByUserEmail(userEmail);

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
                    .email(existingUser.getUserEmail())
                    .userType(existingUser.getUserType())
                    .build();
        } else {
            // New user registration process

            if (isRestrictedUserType(signupRequest.getUserType())) {
                throw new CustomException("Restricted UserType. UserType can only be signed up by the Admin");
            }

            if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
                throw new CustomException("Password does not match");
            }

            String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

            User user = new User();
            user.setUserEmail(signupRequest.getUserEmail());
            user.setFirstName(signupRequest.getFirstName());
            user.setLastName(signupRequest.getLastName());
            user.setPassword(encodedPassword);
            user.setUserType(signupRequest.getUserType());
            user.setEnabled(true);
            user.setLocked(false);

            createNotification(user, "Hello " + user.getFirstName() + "! Welcome onboard!");
            userRepository.save(user);

            return SignupResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getUserEmail())
                    .userType(user.getUserType())
                    .build();
        }
    }

    @Override
    public LoginResponse authenticate(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        User user = userRepository.findByUserEmail(email);
        Organisation organisation = organisationRepository.findByOrgEmail(email);

        if (user == null && organisation == null) {
            throw new CustomException ("User with " + email + " does not exist");
        }
        if (user != null) {
            if (!user.getEnabled()) {
                throw new CustomException("User with " + email + " is not enabled");
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

    private void createNotification(User user, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        user.getNotifications().add(notification);
        userRepository.save(user);
    }
}