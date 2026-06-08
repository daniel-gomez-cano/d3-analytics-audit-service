package co.empresa.proyecto_desarrollo3.service;

import co.empresa.proyecto_desarrollo3.model.AuditLog;
import co.empresa.proyecto_desarrollo3.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {

    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public AuditLog save(AuditLog log) {
        log.setCreatedAt(LocalDateTime.now());
        return repository.save(log);
    }

    public List<AuditLog> getAllLogs() {
        return repository.findAll();
    }
}