package com.example.ambulanceserviceprovider.repository;

import com.example.ambulanceserviceprovider.entities.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganisationRepository extends JpaRepository <Organisation, Long> {
    boolean existsByEmail(String email);
    Organisation findByEmail(String email);

    Organisation findByVerificationToken(String verificationToken);
}
