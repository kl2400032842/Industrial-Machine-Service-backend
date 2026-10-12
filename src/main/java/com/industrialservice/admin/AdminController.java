package com.industrialservice.admin;

import com.industrialservice.audit.AuditLogRepository;
import com.industrialservice.audit.AuditService;
import com.industrialservice.roles.Role;
import com.industrialservice.users.User;
import com.industrialservice.users.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

@RestController @RequestMapping("/api/v1/admin") @PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserRepository users; private final PasswordEncoder encoder; private final AuditLogRepository logs; private final AuditService audit;
    public AdminController(UserRepository users,PasswordEncoder encoder,AuditLogRepository logs,AuditService audit){
        this.users=users;this.encoder=encoder;this.logs=logs;this.audit=audit;
    }
    @PatchMapping("/users/{id}/role")
    public UserRoleResponse setRole(@PathVariable Long id,@RequestBody RoleRequest r,
                                    org.springframework.security.core.Authentication auth,
                                    jakarta.servlet.http.HttpServletRequest req){
        User u=users.findById(id).orElseThrow(()->new NoSuchElementException("User not found"));
        u.setRole(r.role()); users.save(u);
        audit.record(auth.getName(),"USER_ROLE_CHANGED","USER",String.valueOf(id),"Role changed to "+r.role(),req.getRemoteAddr());
        return new UserRoleResponse(u.getId(),u.getEmail(),u.getRole());
    }
    @PatchMapping("/users/{id}/enabled")
    public UserRoleResponse setEnabled(@PathVariable Long id,@RequestBody EnabledRequest r,
                                       org.springframework.security.core.Authentication auth,
                                       jakarta.servlet.http.HttpServletRequest req){
        User u=users.findById(id).orElseThrow(()->new NoSuchElementException("User not found"));
        u.setEnabled(r.enabled()); users.save(u);
        audit.record(auth.getName(),"USER_ENABLED_CHANGED","USER",String.valueOf(id),"Enabled: "+r.enabled(),req.getRemoteAddr());
        return new UserRoleResponse(u.getId(),u.getEmail(),u.getRole());
    }
    @GetMapping("/audit-logs")
    public Object auditLogs(){return logs.findTop200ByOrderByCreatedAtDesc();}
    public record RoleRequest(@NotNull Role role){}
    public record EnabledRequest(boolean enabled){}
    public record UserRoleResponse(Long id,String email,Role role){}
}
