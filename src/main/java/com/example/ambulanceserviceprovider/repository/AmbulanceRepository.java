package com.example.ambulanceserviceprovider.repository;

import com.example.ambulanceserviceprovider.constant.AvailabilityStatus;
import com.example.ambulanceserviceprovider.entities.Ambulance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmbulanceRepository extends JpaRepository <Ambulance, Long> {
    List<Ambulance> findByAvailabilityStatus (AvailabilityStatus availabilityStatus);
    boolean existsByPlateNumber (String plateNumber);
}
