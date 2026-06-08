package co.empresa.proyecto_desarrollo3.dto;

import lombok.Data;

@Data
public class NotificationEvent {

    private String recipient;
    private String subject;
    private String body;

}