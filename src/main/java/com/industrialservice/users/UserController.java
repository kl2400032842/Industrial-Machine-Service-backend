package com.industrialservice.users;

import com.industrialservice.auth.AuthDtos;
import com.industrialservice.roles.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserRepository users;
    public UserController(UserRepository users) { this.users=users; }

    @GetMapping("/users/me")
    public AuthDtos.UserResponse me(Authentication auth) {
        return dto(users.findByEmailIgnoreCase(auth.getName()).orElseThrow());
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuthDtos.UserResponse> all() { return users.findAll().stream().map(this::dto).toList(); }

    @GetMapping("/admin/users/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuthDtos.UserResponse> byRole(@PathVariable Role role) {
        return users.findByRole(role).stream().map(this::dto).toList();
    }

    @PatchMapping("/users/me")
    public AuthDtos.UserResponse updateMe(Authentication auth, @RequestBody UpdateProfile request) {
        User user=users.findByEmailIgnoreCase(auth.getName()).orElseThrow();
        if (request.name()!=null && !request.name().isBlank()) user.setName(request.name().trim());
        if (request.contactNumber()!=null && !request.contactNumber().isBlank()) user.setContactNumber(request.contactNumber().trim());
        return dto(users.save(user));
    }

    private AuthDtos.UserResponse dto(User u) {
        return new AuthDtos.UserResponse(u.getId(),u.getName(),u.getEmail(),u.getContactNumber(),u.getRole());
    }
    public record UpdateProfile(String name, String contactNumber) {}
}
