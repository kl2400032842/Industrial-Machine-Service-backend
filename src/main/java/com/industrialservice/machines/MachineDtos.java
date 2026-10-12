package com.industrialservice.machines;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
public final class MachineDtos {
    private MachineDtos(){}
    public record CreateRequest(@NotBlank @Size(max=120) String name, @Size(max=120) String model,
                                @NotBlank @Size(max=120) String serialNumber,
                                @NotBlank @Size(max=150) String companyName){}
    public record Response(Long id,String name,String model,String serialNumber,String companyName,
                           Long customerId,Instant createdAt){}
}
