package co.empresa.proyecto_desarrollo3.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.empresa.proyecto_desarrollo3.dto.AuditLogRequestDTO;
import co.empresa.proyecto_desarrollo3.dto.AuditLogResponseDTO;
import co.empresa.proyecto_desarrollo3.model.AuditLog;
import co.empresa.proyecto_desarrollo3.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository repository;

    @Transactional
    public AuditLogResponseDTO save(AuditLogRequestDTO request) {
        AuditLog auditLog = AuditLog.builder()
                .eventType(request.getEventType())
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .userId(request.getUserId())
                .payload(request.getPayload())
                .build();

        AuditLog saved = repository.save(auditLog);
        log.info("Audit log saved: eventType={}, entityType={}, entityId={}",
                saved.getEventType(), saved.getEntityType(), saved.getEntityId());
        return toResponse(saved);
    }

    @Transactional
    public void saveInternal(
        String eventType,
        String entityType,
        String entityId,
        String userId,
        String payload) {
        AuditLog auditLog = AuditLog.builder()
                .eventType(eventType)
                .entityType(entityType)
                .entityId(entityId)
                .userId(userId)
                .payload(payload)
                .build();
        repository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponseDTO> getAllLogs() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponseDTO> getLogsByEntityType(
        String entityType,
        String entityId) {
        return repository.findByEntityTypeAndEntityId(entityType, entityId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponseDTO> getLogsByEventType(String eventType) {
        return repository.findByEventType(eventType)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AuditLogResponseDTO toResponse(AuditLog auditLog) {
        return AuditLogResponseDTO.builder()
                .id(auditLog.getId())
                .eventType(auditLog.getEventType())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .userId(auditLog.getUserId())
                .payload(auditLog.getPayload())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}