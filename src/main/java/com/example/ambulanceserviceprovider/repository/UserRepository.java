package com.example.ambulanceserviceprovider.repository;

import com.example.ambulanceserviceprovider.constant.AvailabilityStatus;
import com.example.ambulanceserviceprovider.constant.UserType;
import com.example.ambulanceserviceprovider.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository <User, Long> {
    boolean existsByEmail(String userEmail);
    User findByEmail(String userEmail);
    List<User> findByUserTypeAndAvailabilityStatus (UserType userType, AvailabilityStatus availabilityStatus);
//    List<User> findByFirstNameAndLastNameAllIgnoreCase(String firstName, String lastName);
    List<User> findByFirstNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining (String firstName, String lastName);
    User findByVerificationToken(String verificationToken);
}
