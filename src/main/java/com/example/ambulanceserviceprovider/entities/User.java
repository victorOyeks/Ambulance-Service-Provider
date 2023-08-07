package com.example.ambulanceserviceprovider.entities;

import com.example.ambulanceserviceprovider.constant.AvailabilityStatus;
import com.example.ambulanceserviceprovider.constant.UserType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
    private UserType userType;
    private Boolean enabled;
    private Boolean locked;
    private String token;
    private AvailabilityStatus availabilityStatus;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
