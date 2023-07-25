package com.example.ambulanceserviceprovider.databaseLoader;

import com.example.ambulanceserviceprovider.constant.UserType;
import com.example.ambulanceserviceprovider.entities.User;
import com.example.ambulanceserviceprovider.exceptions.CustomException;
import com.example.ambulanceserviceprovider.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultAdminDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.existsByUserEmail("admin@asp.com")) {
            return;
        }
        String encodedPassword = passwordEncoder.encode("password1");
        User admin = User.builder()
                .userEmail("admin@asp.com")
                .userType(UserType.ADMIN)
                .password(encodedPassword)
                .enabled(true)
                .locked(false)
                .build();
        userRepository.save(admin);
    }
}