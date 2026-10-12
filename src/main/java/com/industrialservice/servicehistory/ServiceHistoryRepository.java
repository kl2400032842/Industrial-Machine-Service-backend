package com.industrialservice.servicehistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ServiceHistoryRepository extends JpaRepository<ServiceHistory,Long> {
    List<ServiceHistory> findByMachineIdOrderByServiceDateDesc(Long machineId);
}
