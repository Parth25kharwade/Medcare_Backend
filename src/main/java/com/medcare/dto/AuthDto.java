package com.medcare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDto {

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        private String phone;
        private String specialization;
        private Long hospitalId;

        @NotBlank(message = "Role is required")
        private String role; // "ROLE_DOCTOR" or "ROLE_ADMIN"
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String tokenType = "Bearer";
        private Long userId;
        private String fullName;
        private String email;
        private String role;
        private Long hospitalId;

        public AuthResponse(String token, Long userId, String fullName,
                            String email, String role, Long hospitalId) {
            this.token = token;
            this.userId = userId;
            this.fullName = fullName;
            this.email = email;
            this.role = role;
            this.hospitalId = hospitalId;
        }
    }
}
