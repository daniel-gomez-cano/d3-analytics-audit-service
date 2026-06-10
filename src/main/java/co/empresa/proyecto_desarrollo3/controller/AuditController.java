package co.empresa.proyecto_desarrollo3.controller;

import co.empresa.proyecto_desarrollo3.dto.AuditLogRequestDTO;
import co.empresa.proyecto_desarrollo3.dto.AuditLogResponseDTO;
import co.empresa.proyecto_desarrollo3.service.AuditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService service;

    @GetMapping("/logs")
    public ResponseEntity<List<AuditLogResponseDTO>> getLogs() {
        return ResponseEntity.ok(service.getAllLogs());
    }

    @GetMapping("/logs/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLogResponseDTO>> getLogsByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(service.getLogsByEntityType(entityType, entityId));
    }

    @GetMapping("/logs/event/{eventType}")
    public ResponseEntity<List<AuditLogResponseDTO>> getLogsByEventType(
            @PathVariable String eventType) {
        return ResponseEntity.ok(service.getLogsByEventType(eventType));
    }

    @PostMapping("/logs")
    public ResponseEntity<AuditLogResponseDTO> createLog(
            @Valid @RequestBody AuditLogRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.save(request));
    }
}
