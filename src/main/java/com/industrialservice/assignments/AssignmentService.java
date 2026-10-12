package com.industrialservice.assignments;

import com.industrialservice.audit.AuditService;
import com.industrialservice.roles.Role;
import com.industrialservice.tickets.*;
import com.industrialservice.users.User;
import com.industrialservice.users.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AssignmentService {
    private final EngineerAssignmentRepository assignments; private final TicketRepository tickets; private final UserRepository users; private final AuditService audit;
    public AssignmentService(EngineerAssignmentRepository a,TicketRepository t,UserRepository u,AuditService audit){this.assignments=a;this.tickets=t;this.users=u;this.audit=audit;}
    @Transactional
    public AssignmentDtos.Response assign(AssignmentDtos.CreateRequest r,String adminEmail,String ip){
        User admin=users.findByEmailIgnoreCase(adminEmail).orElseThrow();
        Ticket ticket=tickets.findById(r.ticketId()).orElseThrow(()->new NoSuchElementException("Ticket not found"));
        User engineer=users.findById(r.engineerId()).orElseThrow(()->new NoSuchElementException("Engineer not found"));
        if(engineer.getRole()!=Role.ENGINEER) throw new IllegalArgumentException("Selected user is not an engineer");
        if(ticket.getStatus()==TicketStatus.CLOSED || ticket.getStatus()==TicketStatus.RESOLVED) throw new IllegalArgumentException("Cannot assign a closed or resolved ticket");
        EngineerAssignment a=new EngineerAssignment();a.setTicket(ticket);a.setEngineer(engineer);a.setAssignedBy(admin);a.setStatus(AssignmentStatus.ASSIGNED);
        ticket.setStatus(TicketStatus.ASSIGNED);tickets.save(ticket);
        EngineerAssignment saved=assignments.save(a);
        audit.record(adminEmail,"ENGINEER_ASSIGNED","TICKET",ticket.getTicketNumber(),"Assigned to "+engineer.getEmail(),ip);
        return dto(saved);
    }
    public List<AssignmentDtos.Response> mine(String email){
        User engineer=users.findByEmailIgnoreCase(email).orElseThrow();
        if(engineer.getRole()!=Role.ENGINEER) throw new AccessDeniedException("Engineer role required");
        return assignments.findByEngineerIdOrderByAssignedAtDesc(engineer.getId()).stream().map(this::dto).toList();
    }
    @Transactional
    public AssignmentDtos.Response update(Long id,AssignmentStatus status,String email,String ip){
        User actor=users.findByEmailIgnoreCase(email).orElseThrow();
        EngineerAssignment a=assignments.findById(id).orElseThrow(()->new NoSuchElementException("Assignment not found"));
        if(actor.getRole()!=Role.ADMIN && !a.getEngineer().getId().equals(actor.getId())) throw new AccessDeniedException("This assignment does not belong to you");
        if(status==AssignmentStatus.ASSIGNED || status==AssignmentStatus.DECLINED || status==AssignmentStatus.COMPLETED) {
            if(actor.getRole()!=Role.ADMIN && status==AssignmentStatus.ASSIGNED) throw new AccessDeniedException("Only admin can reassign");
        }
        a.setStatus(status);
        Ticket t=a.getTicket();
        if(status==AssignmentStatus.ACCEPTED) t.setStatus(TicketStatus.ASSIGNED);
        if(status==AssignmentStatus.COMPLETED) t.setStatus(TicketStatus.RESOLVED);
        if(status==AssignmentStatus.DECLINED) t.setStatus(TicketStatus.OPEN);
        tickets.save(t); EngineerAssignment saved=assignments.save(a);
        audit.record(email,"ASSIGNMENT_STATUS_CHANGED","ASSIGNMENT",String.valueOf(id),"Status set to "+status,ip);
        return dto(saved);
    }
    public List<AssignmentDtos.Response> all(){return assignments.findAll().stream().map(this::dto).toList();}
    private AssignmentDtos.Response dto(EngineerAssignment a){return new AssignmentDtos.Response(a.getId(),a.getTicket().getId(),
            a.getTicket().getTicketNumber(),a.getEngineer().getId(),a.getEngineer().getName(),a.getStatus(),a.getAssignedAt());}
}
