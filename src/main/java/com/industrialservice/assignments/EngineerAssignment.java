package com.industrialservice.assignments;
import com.industrialservice.tickets.Ticket;
import com.industrialservice.users.User;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="engineer_assignments")
public class EngineerAssignment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="ticket_id",nullable=false) private Ticket ticket;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="engineer_id",nullable=false) private User engineer;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="assigned_by_id",nullable=false) private User assignedBy;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private AssignmentStatus status=AssignmentStatus.ASSIGNED;
    @Column(nullable=false,updatable=false) private Instant assignedAt=Instant.now();
    public EngineerAssignment(){}
    public Long getId(){return id;} public Ticket getTicket(){return ticket;} public User getEngineer(){return engineer;}
    public User getAssignedBy(){return assignedBy;} public AssignmentStatus getStatus(){return status;} public Instant getAssignedAt(){return assignedAt;}
    public void setTicket(Ticket v){ticket=v;} public void setEngineer(User v){engineer=v;} public void setAssignedBy(User v){assignedBy=v;}
    public void setStatus(AssignmentStatus v){status=v;}
}
