package com.fortune.cookie.business.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface SecurityService {
    ResponseEntity<Map> security(HttpServletRequest request);
}
