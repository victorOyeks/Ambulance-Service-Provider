package com.example.ambulanceserviceprovider.service.serviceImp;

import com.example.ambulanceserviceprovider.constant.AvailabilityStatus;
import com.example.ambulanceserviceprovider.constant.UserType;
import com.example.ambulanceserviceprovider.dto.response.AmbulanceServiceResponse;
import com.example.ambulanceserviceprovider.entities.Ambulance;
import com.example.ambulanceserviceprovider.entities.Notification;
import com.example.ambulanceserviceprovider.entities.User;
import com.example.ambulanceserviceprovider.exceptions.CustomException;
import com.example.ambulanceserviceprovider.repository.AmbulanceRepository;
import com.example.ambulanceserviceprovider.repository.UserRepository;
import com.example.ambulanceserviceprovider.service.AmbulanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AmbulanceServiceImpl implements AmbulanceService {

    private final AmbulanceRepository ambulanceRepository;
    private final UserRepository userRepository;

    public AmbulanceServiceResponse requestAmbulanceService() {
        User user = getAuthenticatedUser();
        UserType userType = user.getUserType();
        if (userType != UserType.INDIVIDUAL && userType != UserType.TRAFFIC_PATROL) {
            throw new CustomException("Invalid user type. Only INDIVIDUAL and TRAFFIC_PATROL are allowed.");
        }

        List<Ambulance> availableAmbulances = ambulanceRepository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        List<User> availableDrivers = userRepository.findByUserTypeAndAvailabilityStatus(UserType.DRIVER, AvailabilityStatus.AVAILABLE);
        List<User> availableDoctors = userRepository.findByUserTypeAndAvailabilityStatus(UserType.DOCTOR, AvailabilityStatus.AVAILABLE);
        List<User> availableAttendees = userRepository.findByUserTypeAndAvailabilityStatus(UserType.ATTENDEE, AvailabilityStatus.AVAILABLE);
        List<User> availableEmployees = userRepository.findByUserTypeAndAvailabilityStatus(UserType.EMPLOYEE, AvailabilityStatus.AVAILABLE);

        if (availableAmbulances.isEmpty() || availableDrivers.isEmpty()) {
            throw new CustomException("Insufficient resources. Unable to fulfill the request.");
        }

        Random random = new Random();

        Ambulance selectedAmbulance = availableAmbulances.get(random.nextInt(availableAmbulances.size()));
        selectedAmbulance.setAvailabilityStatus(AvailabilityStatus.NOT_AVAILABLE);
        ambulanceRepository.save(selectedAmbulance);

        User selectedDriver = availableDrivers.get(random.nextInt(availableDrivers.size()));
        selectedDriver.setAvailabilityStatus(AvailabilityStatus.NOT_AVAILABLE);
        userRepository.save(selectedDriver);

        String doctorName = selectUserName(availableDoctors, random);
        String attendeeName = selectUserName(availableAttendees, random);
        String employeeName = selectUserName(availableEmployees, random);

        AmbulanceServiceResponse response = new AmbulanceServiceResponse();
        response.setAmbulancePlateNumber(selectedAmbulance.getPlateNumber());
        response.setDriverNames(getFullName(selectedDriver));
        response.setDoctorNames(doctorName);
        response.setAttendeeNames(attendeeName);
        response.setEmployeeNames(employeeName);
        response.setMessage("Ambulance provided successfully!!!");

        return response;
    }

    private String getFullName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }

    private String selectUserName(List<User> userList, Random random) {
        if (userList.isEmpty()) {
            return userList + " not available at the moment!!!";
        }

        User selectedUser = userList.get(random.nextInt(userList.size()));
        selectedUser.setAvailabilityStatus(AvailabilityStatus.NOT_AVAILABLE);
        userRepository.save(selectedUser);

        String fullName = getFullName(selectedUser);
        createNotification(selectedUser, "You have been assigned to an ambulance service request.");

        return fullName;
    }

    public String revertUnavailableEntities() {

        User user = getAuthenticatedUser();

        if(!user.getUserType().equals(UserType.ADMIN)) {
            throw new CustomException("You are not permitted to revert!!!");
        }

        List<Ambulance> unavailableAmbulances = ambulanceRepository.findByAvailabilityStatus(AvailabilityStatus.NOT_AVAILABLE);
        List<User> unavailableDrivers = userRepository.findByUserTypeAndAvailabilityStatus(UserType.DRIVER, AvailabilityStatus.NOT_AVAILABLE);
        List<User> unavailableDoctors = userRepository.findByUserTypeAndAvailabilityStatus(UserType.DOCTOR, AvailabilityStatus.NOT_AVAILABLE);
        List<User> unavailableAttendees = userRepository.findByUserTypeAndAvailabilityStatus(UserType.ATTENDEE, AvailabilityStatus.NOT_AVAILABLE);
        List<User> unavailableEmployees = userRepository.findByUserTypeAndAvailabilityStatus(UserType.EMPLOYEE, AvailabilityStatus.NOT_AVAILABLE);

        for (Ambulance ambulance : unavailableAmbulances) {
            ambulance.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            ambulanceRepository.save(ambulance);
        }

        for (User driver : unavailableDrivers) {
            driver.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            userRepository.save(driver);
        }

        for (User doctor : unavailableDoctors) {
            doctor.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            userRepository.save(doctor);
        }

        for (User attendee : unavailableAttendees) {
            attendee.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            userRepository.save(attendee);
        }

        for (User employee : unavailableEmployees) {
            employee.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            userRepository.save(employee);
        }
        return "Ambulance staff have been made available for another call.";
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
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new CustomException("User not found");
        }
        return user;
    }
}
