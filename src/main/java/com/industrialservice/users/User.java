package com.industrialservice.users;

import com.industrialservice.roles.Role;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email"))
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100) private String name;
    @Column(nullable = false, unique = true, length = 150) private String email;
    @Column(name = "contact_number", nullable = false, length = 30) private String contactNumber;
    @Column(name = "password_hash", nullable = false, length = 100) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Role role;
    @Column(nullable = false) private boolean enabled = true;
    @Column(nullable = false, updatable = false) private Instant createdAt = Instant.now();

    public User() {}
    public Long getId(){return id;} public String getName(){return name;} public String getEmail(){return email;}
    public String getContactNumber(){return contactNumber;} public String getPasswordHash(){return passwordHash;}
    public Role getRole(){return role;} public boolean isEnabled(){return enabled;} public Instant getCreatedAt(){return createdAt;}
    public void setName(String v){name=v;} public void setEmail(String v){email=v;}
    public void setContactNumber(String v){contactNumber=v;} public void setPasswordHash(String v){passwordHash=v;}
    public void setRole(Role v){role=v;} public void setEnabled(boolean v){enabled=v;}
}
