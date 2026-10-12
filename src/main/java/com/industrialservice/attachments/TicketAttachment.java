package com.industrialservice.attachments;
import com.industrialservice.tickets.Ticket;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="ticket_attachments")
public class TicketAttachment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="ticket_id",nullable=false) private Ticket ticket;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private AttachmentType type;
    @Column(name="original_filename",nullable=false,length=255) private String originalFilename;
    @Column(name="stored_filename",nullable=false,length=255) private String storedFilename;
    @Column(name="content_type",nullable=false,length=120) private String contentType;
    @Column(name="size_bytes",nullable=false) private long sizeBytes;
    @Column(name="storage_path",nullable=false,length=1000) private String storagePath;
    @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
    public TicketAttachment(){}
    public Long getId(){return id;} public Ticket getTicket(){return ticket;} public AttachmentType getType(){return type;}
    public String getOriginalFilename(){return originalFilename;} public String getStoredFilename(){return storedFilename;}
    public String getContentType(){return contentType;} public long getSizeBytes(){return sizeBytes;} public String getStoragePath(){return storagePath;}
    public Instant getCreatedAt(){return createdAt;}
    public void setTicket(Ticket v){ticket=v;} public void setType(AttachmentType v){type=v;} public void setOriginalFilename(String v){originalFilename=v;}
    public void setStoredFilename(String v){storedFilename=v;} public void setContentType(String v){contentType=v;}
    public void setSizeBytes(long v){sizeBytes=v;} public void setStoragePath(String v){storagePath=v;}
}
