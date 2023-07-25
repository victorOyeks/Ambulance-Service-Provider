package com.example.ambulanceserviceprovider.repository;

import com.example.ambulanceserviceprovider.entities.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganisationRepository extends JpaRepository <Organisation, Long> {
    boolean existsByOrgEmail (String email);
    Organisation findByOrgEmail (String email);
}
