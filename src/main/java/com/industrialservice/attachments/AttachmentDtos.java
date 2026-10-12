package com.industrialservice.attachments;
import java.time.Instant;
public final class AttachmentDtos {
    private AttachmentDtos(){}
    public record Response(Long id,Long ticketId,AttachmentType type,String originalFilename,String contentType,long sizeBytes,Instant createdAt){}
}
