package com.industrialservice.assignments;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EngineerAssignmentRepository extends JpaRepository<EngineerAssignment,Long> {
    List<EngineerAssignment> findByEngineerIdOrderByAssignedAtDesc(Long engineerId);
    List<EngineerAssignment> findByTicketIdOrderByAssignedAtDesc(Long ticketId);
}
