package com.industrialservice.workorders;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/work-orders")
public class WorkOrderController {
    private final WorkOrderService service;
    public WorkOrderController(WorkOrderService service){this.service=service;}
    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public WorkOrderDtos.Response create(@Valid @RequestBody WorkOrderDtos.CreateRequest r,Authentication a){return service.create(r,a.getName());}
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public WorkOrderDtos.Response update(@PathVariable Long id,@Valid @RequestBody WorkOrderDtos.UpdateRequest r,Authentication a){return service.update(id,r,a.getName());}
    @GetMapping("/ticket/{ticketId}")
    public WorkOrderDtos.Response byTicket(@PathVariable Long ticketId,Authentication a){return service.byTicket(ticketId,a.getName());}
}
