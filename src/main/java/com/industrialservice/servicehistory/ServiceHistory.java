package com.industrialservice.servicehistory;
import com.industrialservice.machines.Machine;
import com.industrialservice.tickets.Ticket;
import com.industrialservice.users.User;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="service_history")
public class ServiceHistory {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="machine_id",nullable=false) private Machine machine;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="ticket_id",nullable=false) private Ticket ticket;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="engineer_id",nullable=false) private User engineer;
    @Column(name="service_date",nullable=false) private Instant serviceDate;
    @Column(nullable=false,columnDefinition="TEXT") private String diagnosis;
    @Column(name="repair_details",columnDefinition="TEXT") private String repairDetails;
    @Column(nullable=false,columnDefinition="TEXT") private String resolution;
    @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
    public ServiceHistory(){}
    public Long getId(){return id;} public Machine getMachine(){return machine;} public Ticket getTicket(){return ticket;}
    public User getEngineer(){return engineer;} public Instant getServiceDate(){return serviceDate;} public String getDiagnosis(){return diagnosis;}
    public String getRepairDetails(){return repairDetails;} public String getResolution(){return resolution;} public Instant getCreatedAt(){return createdAt;}
    public void setMachine(Machine v){machine=v;} public void setTicket(Ticket v){ticket=v;} public void setEngineer(User v){engineer=v;}
    public void setServiceDate(Instant v){serviceDate=v;} public void setDiagnosis(String v){diagnosis=v;}
    public void setRepairDetails(String v){repairDetails=v;} public void setResolution(String v){resolution=v;}
}
