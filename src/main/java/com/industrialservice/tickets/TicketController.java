package com.industrialservice.tickets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {
    private final TicketService service;
    public TicketController(TicketService service){this.service=service;}

    @PostMapping
    public TicketDtos.Response create(@Valid @RequestBody TicketDtos.CreateRequest r,Authentication a,HttpServletRequest req){
        return service.create(r,a.getName(),req.getRemoteAddr());
    }
    @GetMapping("/my")
    public List<TicketDtos.Response> mine(Authentication a){return service.mine(a.getName());}
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<TicketDtos.Response> all(){return service.all();}
    @GetMapping("/{id}")
    public TicketDtos.Response get(@PathVariable Long id,Authentication a){
        boolean admin=a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("ROLE_ADMIN"));
        return service.get(id,a.getName(),admin);
    }
    @GetMapping("/number/{ticketNumber}")
    public TicketDtos.Response getByNumber(@PathVariable String ticketNumber,Authentication a){
        boolean admin=a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("ROLE_ADMIN"));
        return service.findByNumber(ticketNumber,a.getName(),admin);
    }
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public TicketDtos.Response status(@PathVariable Long id,@Valid @RequestBody TicketDtos.StatusRequest r,
                                      Authentication a,HttpServletRequest req){
        return service.updateStatus(id,r.status(),a.getName(),req.getRemoteAddr());
    }
}
