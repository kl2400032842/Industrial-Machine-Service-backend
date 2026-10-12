package com.industrialservice.assignments;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
public final class AssignmentDtos {
    private AssignmentDtos(){}
    public record CreateRequest(@NotNull Long ticketId,@NotNull Long engineerId){}
    public record Response(Long id,Long ticketId,String ticketNumber,Long engineerId,String engineerName,
                           AssignmentStatus status,Instant assignedAt){}
    public record StatusRequest(@NotNull AssignmentStatus status){}
}
