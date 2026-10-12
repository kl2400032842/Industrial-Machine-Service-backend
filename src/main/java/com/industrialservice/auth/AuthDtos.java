package com.industrialservice.auth;

import com.industrialservice.roles.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {}
    public record RegisterRequest(@NotBlank @Size(max=100) String name,
                                  @Email @NotBlank @Size(max=150) String email,
                                  @NotBlank @Size(min=8, max=72) String password,
                                  @NotBlank @Size(max=30) String contactNumber) {}
    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
    public record AuthResponse(String token, String tokenType, long expiresInMs,
                               Long userId, String name, String email, Role role) {}
    public record UserResponse(Long id, String name, String email, String contactNumber, Role role) {}
}
