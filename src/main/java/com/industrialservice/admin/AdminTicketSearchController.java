package com.industrialservice.admin;

import com.industrialservice.tickets.TicketDtos;
import com.industrialservice.tickets.TicketRepository;
import com.industrialservice.tickets.Ticket;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/tickets")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTicketSearchController {
    private final TicketRepository tickets;

    public AdminTicketSearchController(TicketRepository tickets) {
        this.tickets = tickets;
    }

    @GetMapping("/search")
    public List<TicketDtos.Response> search(
            @RequestParam(required = false) String ticketNumber,
            @RequestParam(required = false) String machineSerialNumber) {
        String number = blankToNull(ticketNumber);
        String serial = blankToNull(machineSerialNumber);
        return tickets.searchForAdmin(number, serial).stream().map(this::dto).toList();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private TicketDtos.Response dto(Ticket t) {
        return new TicketDtos.Response(t.getId(), t.getTicketNumber(), t.getTitle(), t.getDescription(),
                t.getPriority(), t.getStatus(), t.getCustomer().getId(), t.getMachine().getId(),
                t.getMachine().getSerialNumber(), t.getCreatedAt(), t.getUpdatedAt());
    }
}
