package com.example.ambulanceserviceprovider.entities;

import com.example.ambulanceserviceprovider.constant.OrganisationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "organisations")
public class Organisation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orgId;
    private String orgEmail;
    private String orgName;
    private String orgAddress;
    private String password;
    private String phoneNumber;
    private Boolean enabled;
    private Boolean locked;
    private OrganisationType organisationType;
    private LocalDateTime createdAt = LocalDateTime.now();
}
