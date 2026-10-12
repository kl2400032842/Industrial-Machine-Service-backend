package com.industrialservice.machines;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface MachineRepository extends JpaRepository<Machine,Long> {
    List<Machine> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    Optional<Machine> findBySerialNumberIgnoreCase(String serialNumber);
}
