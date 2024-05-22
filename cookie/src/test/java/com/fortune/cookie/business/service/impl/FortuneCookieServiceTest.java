package com.fortune.cookie.business.service.impl;

import com.fortune.cookie.business.mappers.FortuneCookieMapper;
import com.fortune.cookie.business.repository.FortuneCookieRepository;
import com.fortune.cookie.business.repository.FortuneRepository;
import com.fortune.cookie.business.repository.UserRepository;
import com.fortune.cookie.business.repository.WordRepository;
import com.fortune.cookie.business.repository.model.FortuneCookieDAO;
import com.fortune.cookie.business.repository.model.UserDAO;
import com.fortune.cookie.model.FortuneCookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FortuneCookieServiceTest {

    @Mock
    private FortuneCookieRepository fortuneCookieRepository;

    @Mock
    private FortuneRepository fortuneRepository;

    @Mock
    private WordRepository wordRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FortuneCookieMapper fortuneCookieMapper;

    @InjectMocks
    private FortuneCookieServiceImpl fortuneCookieService;

//    @Test
//    public void testGetFortuneCookiesByUserId() {
//        String email = "test@test.com";
//        UserDAO userDAO = new UserDAO();
//        userDAO.setId(1L);
//        userDAO.setEmail(email);
//        FortuneCookieDAO fortuneCookieDAO = new FortuneCookieDAO();
//        when(userRepository.findByEmail(email)).thenReturn(userDAO);
//        when(fortuneCookieRepository.findByUserDAO(userDAO)).thenReturn(Collections.singletonList(fortuneCookieDAO));
//        FortuneCookie fortuneCookie = new FortuneCookie();
//        when(fortuneCookieMapper.fortuneCookieDAOToFortuneCookie(fortuneCookieDAO)).thenReturn(fortuneCookie);
//        List<FortuneCookie> result = fortuneCookieService.getFortuneCookiesByUserId(email);
//        assertEquals(1, result.size());
//        assertEquals(fortuneCookie, result.get(0));
//    }
}