package co.empresa.proyecto_desarrollo3.controller;

import co.empresa.proyecto_desarrollo3.dto.NotificationRequest;
import co.empresa.proyecto_desarrollo3.dto.NotificationResponse;
import co.empresa.proyecto_desarrollo3.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse sendNotification(
            @RequestBody NotificationRequest request
    ) {
        return notificationService.sendNotification(request);
    }
}