package com.industrialservice.audit;

import com.industrialservice.users.User;
import com.industrialservice.users.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {
    private final AuditLogRepository logs; private final UserRepository users;
    public AuditService(AuditLogRepository logs,UserRepository users){this.logs=logs;this.users=users;}
    @Transactional
    public void record(String email,String action,String type,String id,String details,String ip){
        AuditLog log=new AuditLog();
        if(email!=null) users.findByEmailIgnoreCase(email).ifPresent(log::setActor);
        log.setAction(action);log.setEntityType(type);log.setEntityId(id);log.setDetails(details);log.setIpAddress(ip);
        logs.save(log);
    }
}
