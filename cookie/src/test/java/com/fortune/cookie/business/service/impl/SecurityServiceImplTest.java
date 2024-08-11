package com.fortune.cookie.business.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceImplTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private SecurityServiceImpl securityService;

    @BeforeEach
    void setUp() {
        reset(request, restTemplate);
    }

    @Test
    void security_successfulResponse() {
        String jwtToken = "Bearer testToken";
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("username", "testUser");

        when(request.getHeader("Authorization")).thenReturn(jwtToken);
        when(restTemplate.postForEntity(
                anyString(),
                eq("testToken"),
                eq(Map.class)
        )).thenReturn(new ResponseEntity<>(responseMap, HttpStatus.OK));

        ResponseEntity<Map> response = securityService.security(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testUser", response.getBody().get("username"));
    }

    @Test
    void security_failedResponse() {
        String jwtToken = "Bearer testToken";
        when(request.getHeader("Authorization")).thenReturn(jwtToken);
        when(restTemplate.postForEntity(
                anyString(),
                eq("testToken"),
                eq(Map.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            securityService.security(request);
        });

        assertEquals("Failed to get results from security microservice", exception.getMessage());
    }

}
