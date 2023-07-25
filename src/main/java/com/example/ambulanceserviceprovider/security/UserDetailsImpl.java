package com.example.ambulanceserviceprovider.security;

import com.example.ambulanceserviceprovider.entities.Organisation;
import com.example.ambulanceserviceprovider.entities.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDetailsImpl implements UserDetails {

    private final User user;
    private final Organisation organisation;

    public UserDetailsImpl(User user, Organisation organisation) {
        this.user = user;
        this.organisation = organisation;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> roles = new ArrayList<>();

        if (user != null && user.getUserType() != null) {
            roles.add(user.getUserType().getName());
        }
        if (organisation != null && organisation.getOrganisationType() != null) {
            roles.add(organisation.getOrganisationType().getName());
        }

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        if (user != null) {
            return user.getPassword();
        }
        if (organisation != null) {
            return organisation.getPassword();
        }
        return null;
    }

    @Override
    public String getUsername() {
        if (user != null) {
            return user.getUserEmail();
        }
        if (organisation != null) {
            return organisation.getOrgEmail();
        }
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
