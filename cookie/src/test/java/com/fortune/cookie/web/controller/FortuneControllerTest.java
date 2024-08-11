package com.fortune.cookie.web.controller;

import com.fortune.cookie.business.service.FortuneService;
import com.fortune.cookie.config.JwtAuthenticationFilter;
import com.fortune.cookie.model.Fortune;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FortuneControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FortuneService fortuneService;

    private Page<Fortune> fortunePage;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Fortune testFortune;

    @BeforeEach
    public void setup() throws ServletException, IOException {
        Fortune testFortune = new Fortune();
        testFortune.setId(1L);
        testFortune.setSentence("You will have a great day!");

        fortunePage = new PageImpl<>(Collections.singletonList(testFortune), PageRequest.of(0, 10), 1);

        when(fortuneService.getAllFortunes(0)).thenReturn(fortunePage);
        when(fortuneService.getFortuneById(1L)).thenReturn(testFortune);
        when(fortuneService.getRandomFortune()).thenReturn(testFortune);

        // mock jwt filtering, because not relevant in current ms
        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class), any(FilterChain.class));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllFortunes_withAdminRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/fortunes")
                        .param("page", "0")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllFortunes_withUserRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/fortunes")
                        .param("page", "0")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getFortuneById_withAdminRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/fortunes/{id}", 1L)
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void getFortuneById_withAdminRole_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/fortunes/{id}", 2L)
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getRandomFortune_withAdminRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/fortune/random")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void getRandomFortune_withAdminRole_shouldReturn404() throws Exception {
        when(fortuneService.getRandomFortune()).thenReturn(null);
        mockMvc.perform(get("/api/v1/fortune/random")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteFortune_withAdminRole_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/fortunes/{id}", 1L)
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void createFortune_withAdminRole_shouldReturn201() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("sentence", "You will find luck today.");
        requestBody.put("neededWords", Collections.emptyList());

        mockMvc.perform(post("/api/v1/fortunes")
                        .header("Authorization", "Bearer valid_token")
                        .contentType("application/json")
                        .content("{\"sentence\":\"You will find luck today.\",\"neededWords\":[]}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateFortune_withAdminRole_shouldReturn201() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("sentence", "Updated fortune.");
        requestBody.put("neededWords", Collections.emptyList());

        mockMvc.perform(put("/api/v1/fortunes/{id}", 1L)
                        .header("Authorization", "Bearer valid_token")
                        .contentType("application/json")
                        .content("{\"sentence\":\"Updated fortune.\",\"neededWords\":[]}"))
                .andExpect(status().isCreated());
    }

}
