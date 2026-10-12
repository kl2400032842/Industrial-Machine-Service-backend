package com.industrialservice.attachments;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController @RequestMapping("/api/v1")
public class AttachmentController {
    private final AttachmentService service;
    public AttachmentController(AttachmentService service){this.service=service;}
    @PostMapping(value="/tickets/{ticketId}/attachments",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public AttachmentDtos.Response upload(@PathVariable Long ticketId,@RequestParam("file") MultipartFile file,
            @RequestParam AttachmentType type,Authentication a,HttpServletRequest req) throws IOException {
        boolean admin=a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("ROLE_ADMIN"));
        return service.upload(ticketId,file,type,a.getName(),admin,req.getRemoteAddr());
    }
    @GetMapping("/tickets/{ticketId}/attachments")
    public List<AttachmentDtos.Response> list(@PathVariable Long ticketId,Authentication a){
        boolean admin=a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("ROLE_ADMIN"));
        return service.list(ticketId,a.getName(),admin);
    }
    @GetMapping("/attachments/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id,Authentication a) throws IOException {
        boolean admin=a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("ROLE_ADMIN"));
        AttachmentService.Download d=service.download(id,a.getName(),admin);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(d.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+d.filename().replace("\"","")+"\"")
                .body(d.resource());
    }
}
