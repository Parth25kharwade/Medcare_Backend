package com.medcare.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medcare.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String email;
    @JsonIgnore
    private final String password;
    private final Long hospitalId;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String email, String password,
                           Long hospitalId, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.hospitalId = hospitalId;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(user.getRole().getName())
        );
        Long hospitalId = user.getHospital() != null ? user.getHospital().getId() : null;
        return new UserDetailsImpl(user.getId(), user.getEmail(),
                user.getPassword(), hospitalId, authorities);
    }

    public Long getId() { return id; }
    public Long getHospitalId() { return hospitalId; }

    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return password; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
