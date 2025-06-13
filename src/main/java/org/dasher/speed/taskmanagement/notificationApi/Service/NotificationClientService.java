package org.dasher.speed.taskmanagement.notificationApi.Service;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.dasher.speed.taskmanagement.domain.NotificationMessage;

@Service
public class NotificationClientService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:8080/api/notifications";
    
    public List<NotificationMessage> getAllNotificationsByReceiverId(Long ReceiverId) {
        String url = UriComponentsBuilder
                .fromUriString(this.url)
                .queryParam("userId", ReceiverId)
                .toUriString();

        NotificationMessage[] response = restTemplate.getForObject(url, NotificationMessage[].class);

        return Arrays.asList(response);
    }
}
