package com.industrialservice.audit;

import com.industrialservice.users.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="audit_logs",indexes=@Index(name="idx_audit_created_at",columnList="created_at"))
public class AuditLog {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="actor_user_id") private User actor;
    @Column(nullable=false,length=100) private String action;
    @Column(name="entity_type",nullable=false,length=100) private String entityType;
    @Column(name="entity_id",nullable=false,length=100) private String entityId;
    @Column(columnDefinition="TEXT") private String details;
    @Column(name="ip_address",length=64) private String ipAddress;
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt=Instant.now();
    public AuditLog(){}
    public Long getId(){return id;} public User getActor(){return actor;} public String getAction(){return action;}
    public String getEntityType(){return entityType;} public String getEntityId(){return entityId;}
    public String getDetails(){return details;} public String getIpAddress(){return ipAddress;} public Instant getCreatedAt(){return createdAt;}
    public void setActor(User v){actor=v;} public void setAction(String v){action=v;} public void setEntityType(String v){entityType=v;}
    public void setEntityId(String v){entityId=v;} public void setDetails(String v){details=v;} public void setIpAddress(String v){ipAddress=v;}
}
