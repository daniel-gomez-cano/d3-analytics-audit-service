package co.empresa.proyecto_desarrollo3.service;

import co.empresa.proyecto_desarrollo3.dto.NotificationRequest;
import co.empresa.proyecto_desarrollo3.dto.NotificationResponse;
import co.empresa.proyecto_desarrollo3.model.Notification;
import co.empresa.proyecto_desarrollo3.model.NotificationStatus;
import co.empresa.proyecto_desarrollo3.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public NotificationResponse sendNotification(NotificationRequest request) {

        Notification notification = new Notification();

                notification.setRecipient(request.getRecipient());
                notification.setSubject(request.getSubject());
                notification.setBody(request.getBody());
                notification.setStatus(NotificationStatus.PENDING);
                notification.setRetries(0);
                notification.setCreatedAt(LocalDateTime.now());

        Notification savedNotification =
                notificationRepository.save(notification);

        return new NotificationResponse(
        savedNotification.getId(),
        savedNotification.getStatus()
);      
    }
}