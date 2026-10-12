package com.industrialservice.tickets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    Optional<Ticket> findByTicketNumber(String ticketNumber);
    List<Ticket> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<Ticket> findAllByOrderByCreatedAtDesc();
    long countByStatus(TicketStatus status);

    @Query("select t from Ticket t join t.machine m where " +
            "(:ticketNumber is null or lower(t.ticketNumber) like lower(concat('%', :ticketNumber, '%'))) and " +
            "(:serialNumber is null or lower(m.serialNumber) like lower(concat('%', :serialNumber, '%'))) " +
            "order by t.createdAt desc")
    List<Ticket> searchForAdmin(@Param("ticketNumber") String ticketNumber,
                                @Param("serialNumber") String serialNumber);
}
