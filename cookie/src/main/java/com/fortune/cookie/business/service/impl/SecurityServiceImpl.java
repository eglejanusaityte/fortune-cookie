package com.fortune.cookie.business.service.impl;

import com.fortune.cookie.business.service.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SecurityServiceImpl implements SecurityService {
    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<Map> security(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String jwtToken = authorizationHeader.substring(7);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://localhost:8081/api/v1/jwt/extract-username",
                jwtToken,
                Map.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Failed to get results from security microservice");
        }
        return response;
    }
}
