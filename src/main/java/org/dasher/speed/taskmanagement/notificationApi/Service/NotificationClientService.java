package org.dasher.speed.taskmanagement.notificationApi.Service;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.dasher.speed.taskmanagement.domain.NotificationMessage;
import org.dasher.speed.taskmanagement.security.JwtTokenService;
import org.dasher.speed.taskmanagement.security.SecurityService;

public class NotificationClientService {

    private final RestTemplate restTemplate;
    private final String url = "http://localhost:8080/notification/api/notifications";
    private final JwtTokenService jwtTokenService;
    private final SecurityService securityService;
    
    @Autowired
    public NotificationClientService(JwtTokenService jwtTokenService, SecurityService securityService) {
        this.jwtTokenService = jwtTokenService;
        this.securityService = securityService;
        this.restTemplate = createRestTemplateWithJwtInterceptor();
    }
    
    private RestTemplate createRestTemplateWithJwtInterceptor() {
        RestTemplate template = new RestTemplate();
        template.getInterceptors().add(jwtInterceptor());
        return template;
    }
    
    private ClientHttpRequestInterceptor jwtInterceptor() {
        return (request, body, execution) -> {
            try {
                var authenticatedUser = securityService.getAuthenticatedUser();
                if (authenticatedUser != null) {
                    String token = jwtTokenService.generateToken(authenticatedUser);
                    request.getHeaders().set("Authorization", "Bearer " + token);
                }
            } catch (Exception e) {
                // Log the error but continue with the request
                System.err.println("Error adding JWT token to request: " + e.getMessage());
            }
            return execution.execute(request, body);
        };
    }
    
    public List<NotificationMessage> getAllNotificationsByReceiverId(Long ReceiverId) {
        String url = UriComponentsBuilder
                .fromUriString(this.url)
                .queryParam("userId", ReceiverId)
                .toUriString();

        NotificationMessage[] response = restTemplate.getForObject(url, NotificationMessage[].class);

        return Arrays.asList(response);
    }

    public Integer getCountNotificationsByReceiverId(Long ReceiverId) {
        String url = UriComponentsBuilder
                .fromUriString(this.url + "/count")
                .queryParam("userId", ReceiverId)
                .toUriString();

        var response = restTemplate.getForObject(url, Integer.class);
        return response;
    }

    public void updateNotification(NotificationMessage notification) {
        String url = UriComponentsBuilder
                .fromUriString(this.url + "/" + notification.getId())
                .toUriString();

        restTemplate.put(url, notification);
    }
}
