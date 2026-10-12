package com.industrialservice.auth;

import com.industrialservice.common.security.JwtService;
import com.industrialservice.roles.Role;
import com.industrialservice.users.User;
import com.industrialservice.users.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final long expirationMs;

    public AuthService(UserRepository users, PasswordEncoder encoder, AuthenticationManager authenticationManager,
                       JwtService jwtService, @Value("${app.jwt.expiration-ms:3600000}") long expirationMs) {
        this.users=users; this.encoder=encoder; this.authenticationManager=authenticationManager;
        this.jwtService=jwtService; this.expirationMs=expirationMs;
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (users.existsByEmailIgnoreCase(request.email())) throw new IllegalArgumentException("Email is already registered");
        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setContactNumber(request.contactNumber().trim());
        user.setPasswordHash(encoder.encode(request.password()));
        user.setRole(Role.CUSTOMER);
        user.setEnabled(true);
        users.save(user);
        return response(user);
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.email().trim().toLowerCase(), request.password()));
        User user = users.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        return response(user);
    }

    private AuthDtos.AuthResponse response(User user) {
        return new AuthDtos.AuthResponse(jwtService.generateToken(user), "Bearer", expirationMs,
                user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
