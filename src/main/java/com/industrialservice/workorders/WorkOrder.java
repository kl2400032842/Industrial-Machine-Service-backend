package com.industrialservice.workorders;
import com.industrialservice.tickets.Ticket;
import com.industrialservice.users.User;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="work_orders")
public class WorkOrder {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="ticket_id",nullable=false,unique=true) private Ticket ticket;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="engineer_id",nullable=false) private User engineer;
    @Column(columnDefinition="TEXT") private String diagnosis;
    @Column(name="work_performed",columnDefinition="TEXT") private String workPerformed;
    @Column(name="repair_outcome",columnDefinition="TEXT") private String repairOutcome;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private WorkOrderStatus status=WorkOrderStatus.NOT_STARTED;
    private Instant startedAt; private Instant completedAt;
    public WorkOrder(){}
    public Long getId(){return id;} public Ticket getTicket(){return ticket;} public User getEngineer(){return engineer;}
    public String getDiagnosis(){return diagnosis;} public String getWorkPerformed(){return workPerformed;}
    public String getRepairOutcome(){return repairOutcome;} public WorkOrderStatus getStatus(){return status;}
    public Instant getStartedAt(){return startedAt;} public Instant getCompletedAt(){return completedAt;}
    public void setTicket(Ticket v){ticket=v;} public void setEngineer(User v){engineer=v;} public void setDiagnosis(String v){diagnosis=v;}
    public void setWorkPerformed(String v){workPerformed=v;} public void setRepairOutcome(String v){repairOutcome=v;}
    public void setStatus(WorkOrderStatus v){status=v;} public void setStartedAt(Instant v){startedAt=v;} public void setCompletedAt(Instant v){completedAt=v;}
}
