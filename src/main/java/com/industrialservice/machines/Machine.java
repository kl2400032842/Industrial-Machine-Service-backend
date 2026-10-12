package com.industrialservice.machines;

import com.industrialservice.users.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="machines", indexes=@Index(name="idx_machine_serial", columnList="serial_number"))
public class Machine {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=120) private String name;
    @Column(length=120) private String model;
    @Column(name="serial_number",nullable=false,length=120) private String serialNumber;
    @Column(name="company_name",nullable=false,length=150) private String companyName;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="customer_id",nullable=false) private User customer;
    @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
    public Machine(){}
    public Long getId(){return id;} public String getName(){return name;} public String getModel(){return model;}
    public String getSerialNumber(){return serialNumber;} public String getCompanyName(){return companyName;}
    public User getCustomer(){return customer;} public Instant getCreatedAt(){return createdAt;}
    public void setName(String v){name=v;} public void setModel(String v){model=v;}
    public void setSerialNumber(String v){serialNumber=v;} public void setCompanyName(String v){companyName=v;}
    public void setCustomer(User v){customer=v;}
}
