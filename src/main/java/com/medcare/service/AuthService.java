package com.medcare.service;

import com.medcare.dto.AuthDto;
import com.medcare.entity.*;
import com.medcare.exception.BadRequestException;
import com.medcare.exception.ResourceNotFoundException;
import com.medcare.repository.*;
import com.medcare.security.JwtUtils;
import com.medcare.security.UserDetailsImpl;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HospitalRepository hospitalRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       HospitalRepository hospitalRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hospitalRepository = hospitalRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new BadRequestException("Role not found: " + request.getRole()));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .specialization(request.getSpecialization())
                .role(role)
                .isActive(true)
                .build();

        if (request.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
            user.setHospital(hospital);
        }

        userRepository.save(user);

        String token = jwtUtils.generateTokenFromEmail(user.getEmail());
        Long hospitalId = user.getHospital() != null ? user.getHospital().getId() : null;

        return new AuthDto.AuthResponse(token, user.getId(), user.getFullName(),
                user.getEmail(), role.getName(), hospitalId);
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = jwtUtils.generateTokenFromEmail(request.getEmail());
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String roleName = userDetails.getAuthorities().iterator().next().getAuthority();

        return new AuthDto.AuthResponse(token, userDetails.getId(), null,
                userDetails.getUsername(), roleName, userDetails.getHospitalId());
    }
}
