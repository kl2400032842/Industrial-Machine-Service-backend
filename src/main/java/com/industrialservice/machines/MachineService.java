package com.industrialservice.machines;

import com.industrialservice.roles.Role;
import com.industrialservice.users.User;
import com.industrialservice.users.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MachineService {
    private final MachineRepository machines; private final UserRepository users;
    public MachineService(MachineRepository machines,UserRepository users){this.machines=machines;this.users=users;}

    @Transactional
    public MachineDtos.Response create(MachineDtos.CreateRequest r,String email){
        if(machines.findBySerialNumberIgnoreCase(r.serialNumber()).isPresent())
            throw new IllegalArgumentException("A machine with this serial number already exists");
        User customer=users.findByEmailIgnoreCase(email).orElseThrow();
        Machine m=new Machine(); m.setName(r.name().trim());m.setModel(r.model());m.setSerialNumber(r.serialNumber().trim());
        m.setCompanyName(r.companyName().trim());m.setCustomer(customer);
        return dto(machines.save(m));
    }
    public List<MachineDtos.Response> mine(String email){
        User user=users.findByEmailIgnoreCase(email).orElseThrow();
        return machines.findByCustomerIdOrderByCreatedAtDesc(user.getId()).stream().map(this::dto).toList();
    }
    public List<MachineDtos.Response> all(){
        return machines.findAll().stream().map(this::dto).toList();
    }
    public Machine getAuthorized(Long id,String email,boolean admin){
        Machine m=machines.findById(id).orElseThrow(()->new NoSuchElementException("Machine not found"));
        if(!admin && !m.getCustomer().getEmail().equalsIgnoreCase(email)) throw new AccessDeniedException("Not your machine");
        return m;
    }
    public MachineDtos.Response get(Long id,String email,boolean admin){return dto(getAuthorized(id,email,admin));}
    private MachineDtos.Response dto(Machine m){return new MachineDtos.Response(m.getId(),m.getName(),m.getModel(),
            m.getSerialNumber(),m.getCompanyName(),m.getCustomer().getId(),m.getCreatedAt());}
}
