package com.example.ambulanceserviceprovider.entities;

import com.example.ambulanceserviceprovider.constant.OrganisationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    private String email;
    private String orgName;
    private String orgAddress;
    private String password;
    private String phoneNumber;
    private Boolean enabled;
    private Boolean locked;
    private String verificationToken;
    private OrganisationType organisationType;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
