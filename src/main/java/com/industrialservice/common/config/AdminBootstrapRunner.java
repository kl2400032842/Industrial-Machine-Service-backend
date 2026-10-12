package com.industrialservice.common.config;

import com.industrialservice.roles.Role;
import com.industrialservice.users.User;
import com.industrialservice.users.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrapRunner implements CommandLineRunner {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;
    private final String name;
    private final String contactNumber;

    public AdminBootstrapRunner(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.email:}") String email,
            @Value("${app.bootstrap-admin.password:}") String password,
            @Value("${app.bootstrap-admin.name:Initial Admin}") String name,
            @Value("${app.bootstrap-admin.contact-number:}") String contactNumber) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
        this.name = name;
        this.contactNumber = contactNumber;
    }

    @Override
    public void run(String... args) {
        if (email.isBlank() || password.isBlank() || contactNumber.isBlank()) {
            return;
        }
        if (users.existsByEmailIgnoreCase(email)) {
            return;
        }
        if (password.length() < 12) {
            throw new IllegalStateException("Bootstrap admin password must be at least 12 characters");
        }
        User admin = new User();
        admin.setName(name);
        admin.setEmail(email.trim().toLowerCase());
        admin.setContactNumber(contactNumber.trim());
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);
        users.save(admin);
    }
}
