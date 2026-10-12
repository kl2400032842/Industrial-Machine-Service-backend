package com.industrialservice.workorders;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
public final class WorkOrderDtos {
    private WorkOrderDtos(){}
    public record CreateRequest(@NotNull Long ticketId){}
    public record UpdateRequest(String diagnosis,String workPerformed,String repairOutcome,@NotNull WorkOrderStatus status){}
    public record Response(Long id,Long ticketId,String ticketNumber,Long engineerId,String diagnosis,String workPerformed,
                           String repairOutcome,WorkOrderStatus status,Instant startedAt,Instant completedAt){}
}
