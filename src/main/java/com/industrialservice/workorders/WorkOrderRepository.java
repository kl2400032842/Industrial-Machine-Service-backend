package com.industrialservice.workorders;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface WorkOrderRepository extends JpaRepository<WorkOrder,Long> { Optional<WorkOrder> findByTicketId(Long ticketId); }
