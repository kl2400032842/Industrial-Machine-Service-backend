package com.industrialservice.tickets;

import com.industrialservice.audit.AuditService;
import com.industrialservice.machines.Machine;
import com.industrialservice.machines.MachineService;
import com.industrialservice.roles.Role;
import com.industrialservice.users.User;
import com.industrialservice.users.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TicketService {
    private final TicketRepository tickets; private final UserRepository users; private final MachineService machines;
    private final AuditService audit;
    public TicketService(TicketRepository tickets,UserRepository users,MachineService machines,AuditService audit){
        this.tickets=tickets;this.users=users;this.machines=machines;this.audit=audit;
    }
    @Transactional
    public TicketDtos.Response create(TicketDtos.CreateRequest r,String email,String ip){
        User customer=users.findByEmailIgnoreCase(email).orElseThrow();
        Machine machine=machines.getAuthorized(r.machineId(),email,false);
        Ticket t=new Ticket();t.setTicketNumber("IMS-"+UUID.randomUUID().toString().substring(0,8).toUpperCase());
        t.setTitle(r.title().trim());t.setDescription(r.description().trim());t.setPriority(r.priority());
        t.setStatus(TicketStatus.OPEN);t.setCustomer(customer);t.setMachine(machine);
        Ticket saved=tickets.save(t);
        audit.record(email,"TICKET_CREATED","TICKET",saved.getTicketNumber(),"Ticket created",ip);
        return dto(saved);
    }
    public List<TicketDtos.Response> mine(String email){
        User u=users.findByEmailIgnoreCase(email).orElseThrow();
        return tickets.findByCustomerIdOrderByCreatedAtDesc(u.getId()).stream().map(this::dto).toList();
    }
    public List<TicketDtos.Response> all(){return tickets.findAllByOrderByCreatedAtDesc().stream().map(this::dto).toList();}
    public Ticket getAuthorized(Long id,String email,boolean admin){
        Ticket t=tickets.findById(id).orElseThrow(()->new NoSuchElementException("Ticket not found"));
        if(!admin && !t.getCustomer().getEmail().equalsIgnoreCase(email)) throw new AccessDeniedException("You cannot access this ticket");
        return t;
    }
    public TicketDtos.Response get(Long id,String email,boolean admin){return dto(getAuthorized(id,email,admin));}
    @Transactional
    public TicketDtos.Response updateStatus(Long id,TicketStatus status,String email,String ip){
        Ticket t=tickets.findById(id).orElseThrow(()->new NoSuchElementException("Ticket not found"));
        TicketStatus old=t.getStatus();
        boolean admin=users.findByEmailIgnoreCase(email).orElseThrow().getRole()==Role.ADMIN;
        boolean engineerAllowed=false;
        if(!admin) throw new AccessDeniedException("Only an administrator can update ticket status directly; engineers update through assignment/workflow APIs");
        validateTransition(old,status);
        t.setStatus(status);
        Ticket saved=tickets.save(t);
        audit.record(email,"TICKET_STATUS_CHANGED","TICKET",saved.getTicketNumber(),old+" -> "+status,ip);
        return dto(saved);
    }
    public TicketDtos.Response findByNumber(String number,String email,boolean admin){
        Ticket t=tickets.findByTicketNumber(number).orElseThrow(()->new NoSuchElementException("Ticket not found"));
        if(!admin && !t.getCustomer().getEmail().equalsIgnoreCase(email)) throw new AccessDeniedException("You cannot access this ticket");
        return dto(t);
    }
    private void validateTransition(TicketStatus from,TicketStatus to){
        boolean ok=switch(from){
            case OPEN -> to==TicketStatus.ASSIGNED || to==TicketStatus.CLOSED;
            case ASSIGNED -> to==TicketStatus.IN_PROGRESS || to==TicketStatus.OPEN || to==TicketStatus.CLOSED;
            case IN_PROGRESS -> to==TicketStatus.RESOLVED || to==TicketStatus.ASSIGNED;
            case RESOLVED -> to==TicketStatus.CLOSED || to==TicketStatus.IN_PROGRESS;
            case CLOSED -> false;
        };
        if(!ok) throw new IllegalArgumentException("Invalid ticket status transition: "+from+" -> "+to);
    }
    private TicketDtos.Response dto(Ticket t){return new TicketDtos.Response(t.getId(),t.getTicketNumber(),t.getTitle(),t.getDescription(),
            t.getPriority(),t.getStatus(),t.getCustomer().getId(),t.getMachine().getId(),t.getMachine().getSerialNumber(),
            t.getCreatedAt(),t.getUpdatedAt());}
}
