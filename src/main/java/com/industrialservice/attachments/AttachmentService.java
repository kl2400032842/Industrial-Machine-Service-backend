package com.industrialservice.attachments;

import com.industrialservice.audit.AuditService;
import com.industrialservice.tickets.Ticket;
import com.industrialservice.tickets.TicketService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AttachmentService {
    private final TicketAttachmentRepository attachments; private final TicketService tickets; private final Path root; private final AuditService audit;
    public AttachmentService(TicketAttachmentRepository a,TicketService t,@Value("${app.storage.local-directory:./uploads}") String directory,AuditService audit) throws IOException {
        attachments=a;tickets=t;this.audit=audit;root=Paths.get(directory).toAbsolutePath().normalize();Files.createDirectories(root);
    }
    @Transactional
    public AttachmentDtos.Response upload(Long ticketId,MultipartFile file,AttachmentType type,String email,boolean admin,String ip) throws IOException {
        if(file==null || file.isEmpty()) throw new IllegalArgumentException("Choose a non-empty file");
        if(file.getSize()>10*1024*1024) throw new IllegalArgumentException("Maximum file size is 10 MB");
        Ticket ticket=tickets.getAuthorized(ticketId,email,admin);
        String content=file.getContentType()==null?"application/octet-stream":file.getContentType().toLowerCase();
        boolean valid=switch(type){
            case IMAGE -> content.equals("image/jpeg") || content.equals("image/png") || content.equals("image/webp");
            case AUDIO -> content.equals("audio/mpeg") || content.equals("audio/mp4") || content.equals("audio/wav")
                    || content.equals("audio/x-wav") || content.equals("audio/webm") || content.equals("audio/ogg");
            case DOCUMENT -> content.equals("application/pdf");
        };
        if(!valid) throw new IllegalArgumentException("File type is not allowed for "+type);
        String original=Path.of(file.getOriginalFilename()==null?"upload.bin":file.getOriginalFilename()).getFileName().toString();
        String stored=UUID.randomUUID().toString();
        Path target=root.resolve(stored).normalize();
        if(!target.startsWith(root)) throw new IllegalArgumentException("Invalid file path");
        Files.copy(file.getInputStream(),target,StandardCopyOption.REPLACE_EXISTING);
        TicketAttachment a=new TicketAttachment();a.setTicket(ticket);a.setType(type);a.setOriginalFilename(original);
        a.setStoredFilename(stored);a.setContentType(content);a.setSizeBytes(file.getSize());a.setStoragePath(target.toString());
        TicketAttachment saved=attachments.save(a);
        audit.record(email,"ATTACHMENT_UPLOADED","TICKET",ticket.getTicketNumber(),type+" attachment uploaded",ip);
        return dto(saved);
    }
    public List<AttachmentDtos.Response> list(Long ticketId,String email,boolean admin){
        tickets.getAuthorized(ticketId,email,admin);
        return attachments.findByTicketIdOrderByCreatedAtDesc(ticketId).stream().map(this::dto).toList();
    }
    public Download download(Long attachmentId,String email,boolean admin) throws IOException {
        TicketAttachment a=attachments.findById(attachmentId).orElseThrow(()->new NoSuchElementException("Attachment not found"));
        tickets.getAuthorized(a.getTicket().getId(),email,admin);
        Path p=Paths.get(a.getStoragePath()).toAbsolutePath().normalize();
        if(!p.startsWith(root) || !Files.exists(p)) throw new NoSuchElementException("File not found");
        return new Download(new UrlResource(p.toUri()),a.getContentType(),a.getOriginalFilename());
    }
    private AttachmentDtos.Response dto(TicketAttachment a){return new AttachmentDtos.Response(a.getId(),a.getTicket().getId(),
            a.getType(),a.getOriginalFilename(),a.getContentType(),a.getSizeBytes(),a.getCreatedAt());}
    public record Download(Resource resource,String contentType,String filename){}
}
