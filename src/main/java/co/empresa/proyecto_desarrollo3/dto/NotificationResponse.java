package co.empresa.proyecto_desarrollo3.dto;

import co.empresa.proyecto_desarrollo3.model.NotificationStatus;

import java.util.UUID;

public class NotificationResponse {

    private UUID id;
    private NotificationStatus status;

    public NotificationResponse() {
    }

    public NotificationResponse(UUID id, NotificationStatus status) {
        this.id = id;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }
}