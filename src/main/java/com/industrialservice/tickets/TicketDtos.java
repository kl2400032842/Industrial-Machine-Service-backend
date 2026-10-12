package com.industrialservice.tickets;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
public final class TicketDtos {
    private TicketDtos(){}
    public record CreateRequest(@NotBlank @Size(max=180) String title,@NotBlank String description,
                                @NotNull TicketPriority priority,@NotNull Long machineId){}
    public record StatusRequest(@NotNull TicketStatus status){}
    public record Response(Long id,String ticketNumber,String title,String description,TicketPriority priority,
                           TicketStatus status,Long customerId,Long machineId,String machineSerialNumber,
                           Instant createdAt,Instant updatedAt){}
}
