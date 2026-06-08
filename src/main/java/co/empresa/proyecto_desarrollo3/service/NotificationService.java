package co.empresa.proyecto_desarrollo3.service;

import co.empresa.proyecto_desarrollo3.dto.NotificationRequest;
import co.empresa.proyecto_desarrollo3.dto.NotificationResponse;

public interface NotificationService {

    NotificationResponse sendNotification(
            NotificationRequest request
    );

}