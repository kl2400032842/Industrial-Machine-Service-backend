package com.industrialservice.servicehistory;

import com.industrialservice.machines.Machine;
import com.industrialservice.machines.MachineService;
import com.industrialservice.tickets.Ticket;
import com.industrialservice.tickets.TicketRepository;
import com.industrialservice.users.User;
import com.industrialservice.users.UserRepository;
import com.industrialservice.roles.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@RestController @RequestMapping("/api/v1/service-history")
public class ServiceHistoryController {
    private final ServiceHistoryRepository history; private final MachineService machines;
    private final TicketRepository tickets; private final UserRepository users;
    public ServiceHistoryController(ServiceHistoryRepository h,MachineService m,TicketRepository t,UserRepository u){
        history=h;machines=m;tickets=t;users=u;
    }
    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public Response create(@Valid @RequestBody CreateRequest r,Authentication a){
        User engineer=users.findByEmailIgnoreCase(a.getName()).orElseThrow();
        Ticket ticket=tickets.findById(r.ticketId()).orElseThrow(()->new NoSuchElementException("Ticket not found"));
        if(engineer.getRole()==Role.ENGINEER && !ticket.getStatus().equals(com.industrialservice.tickets.TicketStatus.RESOLVED))
            throw new IllegalArgumentException("Resolve the ticket before creating final service history");
        Machine machine=ticket.getMachine();
        ServiceHistory h=new ServiceHistory();h.setMachine(machine);h.setTicket(ticket);h.setEngineer(engineer);
        h.setServiceDate(r.serviceDate()==null?Instant.now():r.serviceDate());h.setDiagnosis(r.diagnosis());
        h.setRepairDetails(r.repairDetails());h.setResolution(r.resolution());
        return dto(history.save(h));
    }
    @GetMapping("/machine/{machineId}")
    public List<Response> byMachine(@PathVariable Long machineId,Authentication a){
        boolean admin=a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("ROLE_ADMIN"));
        Machine machine=machines.getAuthorized(machineId,a.getName(),admin);
        return history.findByMachineIdOrderByServiceDateDesc(machine.getId()).stream().map(this::dto).toList();
    }
    private Response dto(ServiceHistory h){return new Response(h.getId(),h.getMachine().getId(),h.getTicket().getId(),
            h.getTicket().getTicketNumber(),h.getEngineer().getId(),h.getEngineer().getName(),h.getServiceDate(),
            h.getDiagnosis(),h.getRepairDetails(),h.getResolution());}
    public record CreateRequest(@NotNull Long ticketId,Instant serviceDate,@NotBlank String diagnosis,
                                String repairDetails,@NotBlank String resolution){}
    public record Response(Long id,Long machineId,Long ticketId,String ticketNumber,Long engineerId,String engineerName,
                           Instant serviceDate,String diagnosis,String repairDetails,String resolution){}
}
