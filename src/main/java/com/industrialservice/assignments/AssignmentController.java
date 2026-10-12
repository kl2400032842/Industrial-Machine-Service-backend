package com.industrialservice.assignments;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/v1/assignments")
public class AssignmentController {
    private final AssignmentService service;
    public AssignmentController(AssignmentService service){this.service=service;}
    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    public AssignmentDtos.Response assign(@Valid @RequestBody AssignmentDtos.CreateRequest r,Authentication a,HttpServletRequest req){
        return service.assign(r,a.getName(),req.getRemoteAddr());
    }
    @GetMapping("/my") @PreAuthorize("hasRole('ENGINEER')")
    public List<AssignmentDtos.Response> mine(Authentication a){return service.mine(a.getName());}
    @GetMapping @PreAuthorize("hasRole('ADMIN')")
    public List<AssignmentDtos.Response> all(){return service.all();}
    @PatchMapping("/{id}/status")
    public AssignmentDtos.Response update(@PathVariable Long id,@Valid @RequestBody AssignmentDtos.StatusRequest r,Authentication a,HttpServletRequest req){
        return service.update(id,r.status(),a.getName(),req.getRemoteAddr());
    }
}
