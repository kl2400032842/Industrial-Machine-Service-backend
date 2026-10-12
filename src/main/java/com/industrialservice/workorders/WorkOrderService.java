package com.industrialservice.workorders;

import com.industrialservice.assignments.EngineerAssignmentRepository;
import com.industrialservice.audit.AuditService;
import com.industrialservice.roles.Role;
import com.industrialservice.tickets.Ticket;
import com.industrialservice.tickets.TicketRepository;
import com.industrialservice.tickets.TicketStatus;
import com.industrialservice.users.User;
import com.industrialservice.users.UserRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.NoSuchElementException;

@Service
public class WorkOrderService {

    private final WorkOrderRepository orders;
    private final TicketRepository tickets;
    private final UserRepository users;
    private final EngineerAssignmentRepository assignments;
    private final AuditService audit;

    public WorkOrderService(
            WorkOrderRepository orders,
            TicketRepository tickets,
            UserRepository users,
            EngineerAssignmentRepository assignments,
            AuditService audit) {
        this.orders = orders;
        this.tickets = tickets;
        this.users = users;
        this.assignments = assignments;
        this.audit = audit;
    }

    @Transactional
    public WorkOrderDtos.Response create(
            WorkOrderDtos.CreateRequest request,
            String email) {

        User actor = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Ticket ticket = tickets.findById(request.ticketId())
                .orElseThrow(() -> new NoSuchElementException("Ticket not found"));

        if (orders.findByTicketId(ticket.getId()).isPresent()) {
            throw new IllegalArgumentException(
                    "A work order already exists for this ticket");
        }

        boolean assignedEngineer = assignments
                .findByTicketIdOrderByAssignedAtDesc(ticket.getId())
                .stream()
                .anyMatch(assignment ->
                        assignment.getEngineer().getId().equals(actor.getId()));

        if (actor.getRole() != Role.ADMIN && !assignedEngineer) {
            throw new AccessDeniedException(
                    "You are not assigned to this ticket");
        }

        WorkOrder workOrder = new WorkOrder();
        workOrder.setTicket(ticket);
        workOrder.setEngineer(actor);
        workOrder.setStatus(WorkOrderStatus.NOT_STARTED);

        WorkOrder saved = orders.save(workOrder);

        audit.record(
                email,
                "WORK_ORDER_CREATED",
                "WORK_ORDER",
                String.valueOf(saved.getId()),
                "Work order created for ticket " + ticket.getTicketNumber(),
                null);

        return dto(saved);
    }

    @Transactional
    public WorkOrderDtos.Response update(
            Long id,
            WorkOrderDtos.UpdateRequest request,
            String email) {

        User actor = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        WorkOrder workOrder = orders.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Work order not found"));

        if (actor.getRole() != Role.ADMIN
                && !workOrder.getEngineer().getId().equals(actor.getId())) {
            throw new AccessDeniedException("Not your work order");
        }

        if (request.status() == WorkOrderStatus.COMPLETED
                && (request.repairOutcome() == null
                || request.repairOutcome().isBlank())) {
            throw new IllegalArgumentException(
                    "Repair outcome is required to complete work");
        }

        workOrder.setDiagnosis(request.diagnosis());
        workOrder.setWorkPerformed(request.workPerformed());
        workOrder.setRepairOutcome(request.repairOutcome());
        workOrder.setStatus(request.status());

        if (request.status() == WorkOrderStatus.IN_PROGRESS
                && workOrder.getStartedAt() == null) {
            workOrder.setStartedAt(Instant.now());
        }

        if (request.status() == WorkOrderStatus.COMPLETED) {
            workOrder.setCompletedAt(Instant.now());
            workOrder.getTicket().setStatus(TicketStatus.RESOLVED);
            tickets.save(workOrder.getTicket());
        }

        WorkOrder saved = orders.save(workOrder);

        audit.record(
                email,
                "WORK_ORDER_UPDATED",
                "WORK_ORDER",
                String.valueOf(saved.getId()),
                "Status: " + saved.getStatus(),
                null);

        return dto(saved);
    }

    @Transactional(readOnly = true)
    public WorkOrderDtos.Response byTicket(Long ticketId, String email) {

        WorkOrder workOrder = orders.findByTicketId(ticketId)
                .orElseThrow(() -> new NoSuchElementException("Work order not found"));

        User actor = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (actor.getRole() != Role.ADMIN
                && actor.getRole() != Role.ENGINEER
                && !workOrder.getTicket().getCustomer().getId()
                        .equals(actor.getId())) {
            throw new AccessDeniedException("Not authorized");
        }

        if (actor.getRole() == Role.ENGINEER
                && !workOrder.getEngineer().getId().equals(actor.getId())) {
            throw new AccessDeniedException("Not your work order");
        }

        return dto(workOrder);
    }

    private WorkOrderDtos.Response dto(WorkOrder workOrder) {
        return new WorkOrderDtos.Response(
                workOrder.getId(),
                workOrder.getTicket().getId(),
                workOrder.getTicket().getTicketNumber(),
                workOrder.getEngineer().getId(),
                workOrder.getDiagnosis(),
                workOrder.getWorkPerformed(),
                workOrder.getRepairOutcome(),
                workOrder.getStatus(),
                workOrder.getStartedAt(),
                workOrder.getCompletedAt());
    }
}