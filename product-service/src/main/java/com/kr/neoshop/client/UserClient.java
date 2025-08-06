package com.kr.neoshop.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.kr.neoshop.DTO.UserDTO;



@Component
public class UserClient {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserClient(RestTemplate restTemplate,
                      @Value("${user.service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public UserDTO getUserByUsername(String username) {
        HttpHeaders headers = new HttpHeaders();
        String token = getTokenFromRequest();
        if (token == null) {
            throw new RuntimeException("No authentication token found");
        }
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<UserDTO> response = restTemplate.exchange(
                userServiceUrl + "/username/" + username,
                HttpMethod.GET,
                entity,
                UserDTO.class
            );

            return response.getBody();
        } catch (HttpClientErrorException.Forbidden e) {
            throw new RuntimeException("Access denied when fetching user details for username: " + username);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("User not found: " + username);
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching user by username: " + e.getMessage(), e);
        }
    }

    private String getTokenFromRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        String authHeader = attrs.getRequest().getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }



    private String getTokenFromContext() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        // Assuming your JWT is stored as the credentials or in the details
        Object credentials = auth.getCredentials();
        if (credentials instanceof String) {
            return (String) credentials;
        }
        return null;
    }

}
