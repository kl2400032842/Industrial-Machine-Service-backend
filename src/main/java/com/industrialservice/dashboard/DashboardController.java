package com.industrialservice.dashboard;

import com.industrialservice.roles.Role;
import com.industrialservice.tickets.TicketRepository;
import com.industrialservice.tickets.TicketStatus;
import com.industrialservice.users.UserRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    private final TicketRepository tickets;
    private final UserRepository users;

    public DashboardController(TicketRepository tickets, UserRepository users) {
        this.tickets = tickets;
        this.users = users;
    }

    @GetMapping("/summary")
    public Map<String, Long> summary() {
        return Map.of(
                "totalTickets", tickets.count(),
                "openTickets", tickets.countByStatus(TicketStatus.OPEN),
                "assignedTickets", tickets.countByStatus(TicketStatus.ASSIGNED),
                "inProgressTickets", tickets.countByStatus(TicketStatus.IN_PROGRESS),
                "resolvedTickets", tickets.countByStatus(TicketStatus.RESOLVED),
                "closedTickets", tickets.countByStatus(TicketStatus.CLOSED),
                "customers", (long) users.findByRole(Role.CUSTOMER).size(),
                "engineers", (long) users.findByRole(Role.ENGINEER).size()
        );
    }
}