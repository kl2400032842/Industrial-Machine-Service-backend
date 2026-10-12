package com.industrialservice.tickets;

import com.industrialservice.machines.Machine;
import com.industrialservice.users.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="tickets",indexes={@Index(name="idx_ticket_number",columnList="ticket_number",unique=true),
        @Index(name="idx_ticket_status",columnList="status")})
public class Ticket {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="ticket_number",nullable=false,unique=true,length=40) private String ticketNumber;
    @Column(nullable=false,length=180) private String title;
    @Column(nullable=false,columnDefinition="TEXT") private String description;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private TicketPriority priority=TicketPriority.MEDIUM;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private TicketStatus status=TicketStatus.OPEN;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="customer_id",nullable=false) private User customer;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="machine_id",nullable=false) private Machine machine;
    @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
    @Column(nullable=false) private Instant updatedAt=Instant.now();
    public Ticket(){}
    @PreUpdate public void preUpdate(){updatedAt=Instant.now();}
    public Long getId(){return id;} public String getTicketNumber(){return ticketNumber;} public String getTitle(){return title;}
    public String getDescription(){return description;} public TicketPriority getPriority(){return priority;}
    public TicketStatus getStatus(){return status;} public User getCustomer(){return customer;} public Machine getMachine(){return machine;}
    public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
    public void setTicketNumber(String v){ticketNumber=v;} public void setTitle(String v){title=v;} public void setDescription(String v){description=v;}
    public void setPriority(TicketPriority v){priority=v;} public void setStatus(TicketStatus v){status=v;}
    public void setCustomer(User v){customer=v;} public void setMachine(Machine v){machine=v;}
}
