package com.fortune.cookie.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortune.cookie.business.dto.FortuneCookieDTO;
import com.fortune.cookie.business.service.FortuneCookieService;
import com.fortune.cookie.business.service.SecurityService;
import com.fortune.cookie.config.JwtAuthenticationFilter;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FortuneCookieControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FortuneCookieService fortuneCookieService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private SecurityService securityService;

    @BeforeEach
    public void setup() throws ServletException, IOException {

        // mock jwt filtering, because not relevant in current ms
        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class), any(FilterChain.class));

        // mock call to another ms
        when(securityService.security(any(HttpServletRequest.class)))
                .thenReturn(new ResponseEntity<>(Map.of("username", "user"), HttpStatus.OK));

    }

    private static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(roles = "USER")
    public void createFortuneCookie_shouldReturn200() throws Exception {
        FortuneCookieDTO fortuneCookieDTO = new FortuneCookieDTO();
        fortuneCookieDTO.setId(1L);
        fortuneCookieDTO.setFortuneCookieSentence("Your fortune is bright!");

        when(fortuneCookieService.createFortuneCookieShort("user")).thenReturn(fortuneCookieDTO);

        mockMvc.perform(post("/api/v1/fortune-cookies")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fortuneCookieSentence").value("Your fortune is bright!"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void createFortuneCookiePersonal_shouldReturn200() throws Exception {
        FortuneCookieDTO fortuneCookieDTO = new FortuneCookieDTO();
        fortuneCookieDTO.setId(1L);
        fortuneCookieDTO.setFortuneCookieSentence("Your personal fortune is unique!");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("fortuneId", 1L);
        requestBody.put("words", Arrays.asList("happy", "life"));

        when(fortuneCookieService.createFortuneCookiePersonal(1L, "user", Arrays.asList("happy", "life")))
                .thenReturn(fortuneCookieDTO);

        mockMvc.perform(post("/api/v1/fortune-cookie-personal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestBody))
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fortuneCookieSentence").value("Your personal fortune is unique!"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getFortuneCookiesByUser_shouldReturnPage() throws Exception {
        FortuneCookieDTO fortuneCookieDTO = new FortuneCookieDTO();
        fortuneCookieDTO.setId(1L);
        fortuneCookieDTO.setFortuneCookieSentence("Your user fortune is special!");

        Page<FortuneCookieDTO> fortuneCookiePage = new PageImpl<>(Collections.singletonList(fortuneCookieDTO), PageRequest.of(0, 10), 1);

        when(fortuneCookieService.getFortuneCookiesByUserId(0, "user")).thenReturn(fortuneCookiePage);

        mockMvc.perform(get("/api/v1/fortune-cookies-personal")
                        .param("page", "0")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].fortuneCookieSentence").value("Your user fortune is special!"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getFortuneCookies_shouldReturnPage() throws Exception {
        FortuneCookieDTO fortuneCookieDTO = new FortuneCookieDTO();
        fortuneCookieDTO.setId(1L);
        fortuneCookieDTO.setFortuneCookieSentence("General fortune cookie!");

        Page<FortuneCookieDTO> fortuneCookiePage = new PageImpl<>(Collections.singletonList(fortuneCookieDTO), PageRequest.of(0, 10), 1);

        when(fortuneCookieService.getFortuneCookies(0)).thenReturn(fortuneCookiePage);

        mockMvc.perform(get("/api/v1/fortune-cookies")
                        .param("page", "0")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].fortuneCookieSentence").value("General fortune cookie!"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteFortuneCookie_shouldReturn204() throws Exception {
        doNothing().when(fortuneCookieService).deleteFortuneCookie(1L, "user");

        mockMvc.perform(delete("/api/v1/fortune-cookie/1")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void likeFortuneCookie_shouldReturn204() throws Exception {
        doNothing().when(fortuneCookieService).likeFortuneCookie(1L, "user");

        mockMvc.perform(put("/api/v1/fortune-cookies/1/like")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void removeLikeFortuneCookie_shouldReturn204() throws Exception {
        doNothing().when(fortuneCookieService).removeLikeFortuneCookie(1L, "user");

        mockMvc.perform(put("/api/v1/fortune-cookies/1/remove")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isNoContent());
    }


}
