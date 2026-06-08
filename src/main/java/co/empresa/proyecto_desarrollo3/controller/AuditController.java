package co.empresa.proyecto_desarrollo3.controller;

import co.empresa.proyecto_desarrollo3.model.AuditLog;
import co.empresa.proyecto_desarrollo3.service.AuditService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditService service;

    public AuditController(AuditService service) {
        this.service = service;
    }

    @GetMapping("/logs")
    public List<AuditLog> getLogs() {
        return service.getAllLogs();
    }

    @PostMapping("/logs")
    public AuditLog createLog(
            @RequestBody AuditLog auditLog
    ) {
        return service.save(auditLog);
    }
}