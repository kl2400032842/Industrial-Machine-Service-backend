package com.industrialservice.machines;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/machines")
public class MachineController {
    private final MachineService service;
    public MachineController(MachineService service){this.service=service;}

    @PostMapping
    public MachineDtos.Response create(@Valid @RequestBody MachineDtos.CreateRequest r,Authentication a){return service.create(r,a.getName());}
    @GetMapping("/my")
    public List<MachineDtos.Response> mine(Authentication a){return service.mine(a.getName());}
    @GetMapping("/{id}")
    public MachineDtos.Response get(@PathVariable Long id,Authentication a){
        return service.get(id,a.getName(),a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("ROLE_ADMIN")));
    }
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<MachineDtos.Response> all(){return service.all();}
}
