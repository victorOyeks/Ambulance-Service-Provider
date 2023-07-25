package com.example.ambulanceserviceprovider.entities;

import com.example.ambulanceserviceprovider.constant.AvailabilityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "ambulance")
public class Ambulance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long AmbulanceId;
    private String plateNumber;
    private AvailabilityStatus availabilityStatus;
    private LocalDateTime createdAt = LocalDateTime.now();
}
