package com.fortune.cookie.business.service.impl;


import com.fortune.cookie.business.dto.FortuneCookieDTO;
import com.fortune.cookie.business.enums.Role;
import com.fortune.cookie.business.enums.WordType;
import com.fortune.cookie.business.mappers.FortuneCookieMapper;
import com.fortune.cookie.business.mappers.UserMapper;
import com.fortune.cookie.business.repository.FortuneCookieRepository;
import com.fortune.cookie.business.repository.FortuneRepository;
import com.fortune.cookie.business.repository.UserRepository;
import com.fortune.cookie.business.repository.WordRepository;
import com.fortune.cookie.business.repository.model.FortuneCookieDAO;
import com.fortune.cookie.business.repository.model.FortuneDAO;
import com.fortune.cookie.business.repository.model.NeededWordDAO;
import com.fortune.cookie.business.repository.model.UserDAO;
import com.fortune.cookie.business.repository.model.WordDAO;
import com.fortune.cookie.model.Fortune;
import com.fortune.cookie.model.FortuneCookie;
import com.fortune.cookie.model.Word;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;

public class FortuneCookieServiceImplTest {
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

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private FortuneCookieServiceImpl fortuneCookieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createFortuneCookie_existingPersonalCookie() {
        String username = "testUser";
        UserDAO userDAO = new UserDAO();
        FortuneCookieDAO existingCookie = new FortuneCookieDAO();
        existingCookie.setWords(Set.of(new WordDAO("happy", WordType.ADJECTIVE, false)));
        when(userRepository.findByUsername(username)).thenReturn(userDAO);
        when(fortuneCookieRepository.findByUserDAOAndDate(userDAO, LocalDate.now())).thenReturn(List.of(existingCookie));
        when(fortuneCookieMapper.fortuneCookieDAOToFortuneCookie(existingCookie)).thenReturn(new FortuneCookie());

        FortuneCookie result = fortuneCookieService.createFortuneCookie(username);

        assertNotNull(result);
        verify(fortuneCookieRepository, never()).save(any());
    }

    @Test
    void createFortuneCookie_newCookie() {
        String username = "testUser";
        UserDAO userDAO = new UserDAO();
        FortuneDAO fortuneDAO = new FortuneDAO();
        List<FortuneDAO> allFortunes = List.of(fortuneDAO);
        List<WordDAO> allWords = List.of(new WordDAO("happy", WordType.ADJECTIVE, false));
        FortuneCookieDAO savedCookie = new FortuneCookieDAO();
        when(userRepository.findByUsername(username)).thenReturn(userDAO);
        when(fortuneCookieRepository.findByUserDAOAndDate(userDAO, LocalDate.now())).thenReturn(Collections.emptyList());
        when(fortuneRepository.findAll()).thenReturn(allFortunes);
        when(wordRepository.findAll()).thenReturn(allWords);
        when(fortuneCookieRepository.save(any(FortuneCookieDAO.class))).thenReturn(savedCookie);
        when(fortuneCookieMapper.fortuneCookieDAOToFortuneCookie(savedCookie)).thenReturn(new FortuneCookie());

        FortuneCookie result = fortuneCookieService.createFortuneCookie(username);

        assertNotNull(result);
        verify(fortuneCookieRepository).save(any(FortuneCookieDAO.class));
    }

    @Test
    void createFortuneCookieShort() {
        String username = "testUser";
        UserDAO userDAO = new UserDAO();
        FortuneCookie fortuneCookie = new FortuneCookie();
        Fortune fortune = new Fortune();
        fortune.setSentence("Test sentence #ADJECTIVE");
        fortuneCookie.setFortune(fortune);
        Word word = new Word(1L, "nice", WordType.ADJECTIVE, false);
        Set<Word> allWords = Set.of(word);
        fortuneCookie.setWords(allWords);
        FortuneCookieDAO existingCookie = new FortuneCookieDAO();
        existingCookie.setWords(Set.of(new WordDAO("happy", WordType.ADJECTIVE, false)));
        when(userRepository.findByUsername(username)).thenReturn(userDAO);
        when(fortuneCookieRepository.findByUserDAOAndDate(userDAO, LocalDate.now())).thenReturn(List.of(existingCookie));
        when(fortuneCookieMapper.fortuneCookieDAOToFortuneCookie(existingCookie)).thenReturn(fortuneCookie);

        FortuneCookieDTO cookieResult = fortuneCookieService.createFortuneCookieShort(username);
        assertEquals(cookieResult.getFortuneCookieSentence(), "Test sentence nice");
    }

    @Test
    void createFortuneCookiePersonal() {
        String username = "testUser";
        List<String> words = List.of("happy");
        UserDAO userDAO = new UserDAO();
        FortuneDAO fortuneDAO = new FortuneDAO();
        fortuneDAO.setSentence("Test sentence #Adjective");
        NeededWordDAO neededWordDAO = new NeededWordDAO(1L, WordType.ADJECTIVE, "Test");
        fortuneDAO.setNeededWordDAOS(Set.of(neededWordDAO));
        FortuneCookieDAO fortuneCookieDAO = new FortuneCookieDAO();
        fortuneCookieDAO.setFortuneDAO(fortuneDAO);
        fortuneCookieDAO.setWords(Set.of(new WordDAO("happy", WordType.ADJECTIVE, false)));
        when(userRepository.findByUsername(username)).thenReturn(userDAO);
        when(fortuneRepository.findById(anyLong())).thenReturn(Optional.of(fortuneDAO));
        when(wordRepository.save(any(WordDAO.class))).thenReturn(new WordDAO());
        when(fortuneCookieRepository.save(any(FortuneCookieDAO.class))).thenReturn(fortuneCookieDAO);

        FortuneCookieDTO result = fortuneCookieService.createFortuneCookiePersonal(1L, username, words);

        assertNotNull(result);
    }

    @Test
    void createFortuneCookiePersonal_throwsIllegalArgumentException() {
        String username = "testUser";
        List<String> words = List.of("happy");
        UserDAO userDAO = new UserDAO();
        FortuneDAO fortuneDAO = new FortuneDAO();
        fortuneDAO.setSentence("Test sentence #Adjective");
        when(userRepository.findByUsername(username)).thenReturn(userDAO);
        when(fortuneRepository.findById(anyLong())).thenReturn(Optional.of(fortuneDAO));

        assertThrows(IllegalArgumentException.class, () ->
                fortuneCookieService.createFortuneCookiePersonal(1L, username, words)
        );
    }

    @Test
    void getFortuneCookiesByUserId_userExists_multipleCookies() {
        String username = "testUser";
        int page = 0;
        UserDAO userDAO = new UserDAO();
        userDAO.setUsername(username);

        FortuneCookieDAO fortuneCookieDAO = new FortuneCookieDAO();
        FortuneDAO fortuneDAO = new FortuneDAO();
        fortuneDAO.setSentence("Test sentence #ADJECTIVE");
        fortuneCookieDAO.setFortuneDAO(fortuneDAO);
        WordDAO wordDAO = new WordDAO("happy", WordType.ADJECTIVE, false);
        fortuneCookieDAO.setWords(Set.of(wordDAO));
        fortuneCookieDAO.setLikes(Set.of(userDAO));

        Page<FortuneCookieDAO> fortuneCookiePage = new PageImpl<>(List.of(fortuneCookieDAO));
        when(userRepository.findByUsername(username)).thenReturn(userDAO);
        when(fortuneCookieRepository.findByUserDAO(userDAO, PageRequest.of(page, 3, Sort.by("date").descending())))
                .thenReturn(fortuneCookiePage);

        Page<FortuneCookieDTO> result = fortuneCookieService.getFortuneCookiesByUserId(page, username);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test sentence happy", result.getContent().get(0).getFortuneCookieSentence());
        assertTrue(result.getContent().get(0).getUsername().contains(username));
    }

    @Test
    void getFortuneCookiesByUserId_userExists_noCookies() {
        String username = "testUser";
        int page = 0;
        UserDAO userDAO = new UserDAO();
        userDAO.setUsername(username);

        Page<FortuneCookieDAO> emptyPage = Page.empty();
        when(userRepository.findByUsername(username)).thenReturn(userDAO);
        when(fortuneCookieRepository.findByUserDAO(userDAO, PageRequest.of(page, 3, Sort.by("date").descending())))
                .thenReturn(emptyPage);

        Page<FortuneCookieDTO> result = fortuneCookieService.getFortuneCookiesByUserId(page, username);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getFortuneCookiesByUserId_userDoesNotExist() {
        String username = "nonExistentUser";
        int page = 0;

        when(userRepository.findByUsername(username)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> {
            fortuneCookieService.getFortuneCookiesByUserId(page, username);
        });
    }

    @Test
    void getFortuneCookiesByUserId_personalWords() {
        String username = "testUser";
        int page = 0;
        UserDAO userDAO = new UserDAO();
        userDAO.setUsername(username);

        FortuneCookieDAO fortuneCookieDAO = new FortuneCookieDAO();
        FortuneDAO fortuneDAO = new FortuneDAO();
        fortuneDAO.setSentence("Test sentence #ADJECTIVE");
        fortuneCookieDAO.setFortuneDAO(fortuneDAO);
        WordDAO wordDAO = new WordDAO("happy", WordType.ADJECTIVE, true);
        fortuneCookieDAO.setWords(Set.of(wordDAO));
        fortuneCookieDAO.setLikes(Set.of(userDAO));

        Page<FortuneCookieDAO> fortuneCookiePage = new PageImpl<>(List.of(fortuneCookieDAO));
        when(userRepository.findByUsername(username)).thenReturn(userDAO);
        when(fortuneCookieRepository.findByUserDAO(userDAO, PageRequest.of(page, 3, Sort.by("date").descending())))
                .thenReturn(fortuneCookiePage);

        Page<FortuneCookieDTO> result = fortuneCookieService.getFortuneCookiesByUserId(page, username);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test sentence happy", result.getContent().get(0).getFortuneCookieSentence());
        assertTrue(result.getContent().get(0).getPersonal());
    }

    @Test
    void getFortuneCookiesByUserId_fortuneCookiesWithLikes() {
        String username = "testUser";
        int page = 0;
        UserDAO userDAO = new UserDAO();
        userDAO.setUsername(username);

        FortuneCookieDAO fortuneCookieDAO = new FortuneCookieDAO();
        FortuneDAO fortuneDAO = new FortuneDAO();
        fortuneDAO.setSentence("Test sentence #ADJECTIVE");
        fortuneCookieDAO.setFortuneDAO(fortuneDAO);
        WordDAO wordDAO = new WordDAO("happy", WordType.ADJECTIVE, false);
        fortuneCookieDAO.setWords(Set.of(wordDAO));
        fortuneCookieDAO.setLikes(Set.of(userDAO));

        Page<FortuneCookieDAO> fortuneCookiePage = new PageImpl<>(List.of(fortuneCookieDAO));
        when(userRepository.findByUsername(username)).thenReturn(userDAO);
        when(fortuneCookieRepository.findByUserDAO(userDAO, PageRequest.of(page, 3, Sort.by("date").descending())))
                .thenReturn(fortuneCookiePage);

        Page<FortuneCookieDTO> result = fortuneCookieService.getFortuneCookiesByUserId(page, username);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test sentence happy", result.getContent().get(0).getFortuneCookieSentence());
        assertTrue(result.getContent().get(0).getUsername().contains(username));
    }

    @Test
    void getFortuneCookies_fortuneCookiesExist() {
        int page = 0;
        LocalDate today = LocalDate.now();
        UserDAO userDAO = new UserDAO();
        userDAO.setUsername("testUser");

        FortuneCookieDAO fortuneCookieDAO = new FortuneCookieDAO();
        FortuneDAO fortuneDAO = new FortuneDAO();
        fortuneDAO.setSentence("Test sentence #ADJECTIVE");
        fortuneCookieDAO.setFortuneDAO(fortuneDAO);
        fortuneCookieDAO.setUserDAO(userDAO);
        WordDAO wordDAO = new WordDAO("happy", WordType.ADJECTIVE, false);
        fortuneCookieDAO.setWords(Set.of(wordDAO));
        fortuneCookieDAO.setLikes(Set.of(userDAO));

        Page<FortuneCookieDAO> fortuneCookiePage = new PageImpl<>(List.of(fortuneCookieDAO));
        when(fortuneCookieRepository.findByDate(today, PageRequest.of(page, 3))).thenReturn(fortuneCookiePage);

        Page<FortuneCookieDTO> result = fortuneCookieService.getFortuneCookies(page);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test sentence happy", result.getContent().get(0).getFortuneCookieSentence());
        assertTrue(result.getContent().get(0).getUsername().contains("testUser"));
    }

    @Test
    void getFortuneCookies_noFortuneCookiesExist() {
        int page = 0;
        LocalDate today = LocalDate.now();

        Page<FortuneCookieDAO> emptyPage = Page.empty();
        when(fortuneCookieRepository.findByDate(today, PageRequest.of(page, 3))).thenReturn(emptyPage);

        Page<FortuneCookieDTO> result = fortuneCookieService.getFortuneCookies(page);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getFortuneCookies_personalWords() {
        int page = 0;
        LocalDate today = LocalDate.now();
        UserDAO userDAO = new UserDAO();
        userDAO.setUsername("testUser");

        FortuneCookieDAO fortuneCookieDAO = new FortuneCookieDAO();
        FortuneDAO fortuneDAO = new FortuneDAO();
        fortuneDAO.setSentence("Test sentence #ADJECTIVE");
        fortuneCookieDAO.setFortuneDAO(fortuneDAO);
        fortuneCookieDAO.setUserDAO(userDAO);
        WordDAO wordDAO = new WordDAO("happy", WordType.ADJECTIVE, true);
        fortuneCookieDAO.setWords(Set.of(wordDAO));
        fortuneCookieDAO.setLikes(Set.of(userDAO));

        Page<FortuneCookieDAO> fortuneCookiePage = new PageImpl<>(List.of(fortuneCookieDAO));
        when(fortuneCookieRepository.findByDate(today, PageRequest.of(page, 3))).thenReturn(fortuneCookiePage);

        Page<FortuneCookieDTO> result = fortuneCookieService.getFortuneCookies(page);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test sentence happy", result.getContent().get(0).getFortuneCookieSentence());
        assertTrue(result.getContent().get(0).getPersonal());
    }

    @Test
    void getFortuneCookies_fortuneCookiesWithLikes() {
        int page = 0;
        LocalDate today = LocalDate.now();
        UserDAO userDAO = new UserDAO();
        userDAO.setUsername("testUser");

        FortuneCookieDAO fortuneCookieDAO = new FortuneCookieDAO();
        FortuneDAO fortuneDAO = new FortuneDAO();
        fortuneDAO.setSentence("Test sentence #ADJECTIVE");
        fortuneCookieDAO.setFortuneDAO(fortuneDAO);
        fortuneCookieDAO.setUserDAO(userDAO);
        WordDAO wordDAO = new WordDAO("happy", WordType.ADJECTIVE, false);
        fortuneCookieDAO.setWords(Set.of(wordDAO));
        fortuneCookieDAO.setLikes(Set.of(userDAO));

        Page<FortuneCookieDAO> fortuneCookiePage = new PageImpl<>(List.of(fortuneCookieDAO));
        when(fortuneCookieRepository.findByDate(today, PageRequest.of(page, 3))).thenReturn(fortuneCookiePage);

        Page<FortuneCookieDTO> result = fortuneCookieService.getFortuneCookies(page);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test sentence happy", result.getContent().get(0).getFortuneCookieSentence());
        assertTrue(result.getContent().get(0).getUsername().contains("testUser"));
    }

    @Test
    void deleteFortuneCookie_successfulDeletionByUser() {
        Long fortuneCookieId = 1L;
        String username = "testUser";
        UserDAO userDAO = new UserDAO();
        userDAO.setId(1L);
        userDAO.setUsername(username);
        userDAO.setRole(Role.USER);

        FortuneCookieDAO fortuneCookieDAO = new FortuneCookieDAO();
        fortuneCookieDAO.setId(fortuneCookieId);
        fortuneCookieDAO.setUserDAO(userDAO);

        when(fortuneCookieRepository.findById(fortuneCookieId)).thenReturn(Optional.of(fortuneCookieDAO));
        when(userRepository.findByUsername(username)).thenReturn(userDAO);

        fortuneCookieService.deleteFortuneCookie(fortuneCookieId, username);

        verify(fortuneCookieRepository, times(1)).delete(fortuneCookieDAO);
    }

    @Test
    void deleteFortuneCookie_successfulDeletionByAdmin() {
        Long fortuneCookieId = 1L;
        String username = "adminUser";
        UserDAO userDAO = new UserDAO();
        userDAO.setId(2L);
        userDAO.setUsername(username);
        userDAO.setRole(Role.ADMIN);

        UserDAO fortuneOwner = new UserDAO();
        fortuneOwner.setId(1L);
        fortuneOwner.setUsername("fortuneOwner");

        FortuneCookieDAO fortuneCookieDAO = new FortuneCookieDAO();
        fortuneCookieDAO.setId(fortuneCookieId);
        fortuneCookieDAO.setUserDAO(fortuneOwner);

        when(fortuneCookieRepository.findById(fortuneCookieId)).thenReturn(Optional.of(fortuneCookieDAO));
        when(userRepository.findByUsername(username)).thenReturn(userDAO);

        fortuneCookieService.deleteFortuneCookie(fortuneCookieId, username);

        verify(fortuneCookieRepository, times(1)).delete(fortuneCookieDAO);
    }

    @Test
    void deleteFortuneCookie_fortuneCookieNotFound() {
        Long fortuneCookieId = 1L;
        String username = "testUser";

        when(fortuneCookieRepository.findById(fortuneCookieId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            fortuneCookieService.deleteFortuneCookie(fortuneCookieId, username);
        });

        assertEquals("Fortune cookie not found", exception.getMessage());
        verify(fortuneCookieRepository, never()).delete(any());
    }

    @Test
    void deleteFortuneCookie_userNotFound() {
        Long fortuneCookieId = 1L;
        String username = "testUser";

        UserDAO fortuneOwner = new UserDAO();
        fortuneOwner.setId(1L);
        fortuneOwner.setUsername("fortuneOwner");

        FortuneCookieDAO fortuneCookieDAO = new FortuneCookieDAO();
        fortuneCookieDAO.setId(fortuneCookieId);
        fortuneCookieDAO.setUserDAO(fortuneOwner);

        when(fortuneCookieRepository.findById(fortuneCookieId)).thenReturn(Optional.of(fortuneCookieDAO));
        when(userRepository.findByUsername(username)).thenReturn(null);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            fortuneCookieService.deleteFortuneCookie(fortuneCookieId, username);
        });

        assertEquals("No value present", exception.getMessage());  // Default message from Optional.orElseThrow()
        verify(fortuneCookieRepository, never()).delete(any());
    }

    @Test
    void deleteFortuneCookie_unauthorizedDeletionAttempt() {
        Long fortuneCookieId = 1L;
        String username = "unauthorizedUser";

        UserDAO unauthorizedUser = new UserDAO();
        unauthorizedUser.setId(2L);
        unauthorizedUser.setUsername(username);
        unauthorizedUser.setRole(Role.USER);

        UserDAO fortuneOwner = new UserDAO();
        fortuneOwner.setId(1L);
        fortuneOwner.setUsername("fortuneOwner");

        FortuneCookieDAO fortuneCookieDAO = new FortuneCookieDAO();
        fortuneCookieDAO.setId(fortuneCookieId);
        fortuneCookieDAO.setUserDAO(fortuneOwner);

        when(fortuneCookieRepository.findById(fortuneCookieId)).thenReturn(Optional.of(fortuneCookieDAO));
        when(userRepository.findByUsername(username)).thenReturn(unauthorizedUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fortuneCookieService.deleteFortuneCookie(fortuneCookieId, username);
        });

        assertEquals("User is not authorized to delete this fortune cookie", exception.getMessage());
        verify(fortuneCookieRepository, never()).delete(any());
    }

    @Test
    void likeFortuneCookie_fortuneCookieFoundAndNotAlreadyLiked() {
        Long fortuneCookieId = 1L;
        String username = "testUser";

        UserDAO userDAO = new UserDAO();
        userDAO.setId(1L);
        userDAO.setUsername(username);

        FortuneCookieDAO fortuneCookieDAO = new FortuneCookieDAO();
        fortuneCookieDAO.setId(fortuneCookieId);
        fortuneCookieDAO.setLikes(new HashSet<>());

        when(fortuneCookieRepository.findById(fortuneCookieId)).thenReturn(Optional.of(fortuneCookieDAO));
        when(userRepository.findByUsername(username)).thenReturn(userDAO);

        fortuneCookieService.likeFortuneCookie(fortuneCookieId, username);

        assertTrue(fortuneCookieDAO.getLikes().contains(userDAO));
        verify(fortuneCookieRepository, times(1)).save(fortuneCookieDAO);
    }

    @Test
    void removeLikeFortuneCookie_fortuneCookieFoundAndUserHasLikedIt() {
        Long fortuneCookieId = 1L;
        String username = "testUser";

        UserDAO userDAO = new UserDAO();
        userDAO.setId(1L);
        userDAO.setUsername(username);

        Set<UserDAO> likes = new HashSet<>();
        likes.add(userDAO);

        FortuneCookieDAO fortuneCookieDAO = new FortuneCookieDAO();
        fortuneCookieDAO.setId(fortuneCookieId);
        fortuneCookieDAO.setLikes(likes);

        when(fortuneCookieRepository.findById(fortuneCookieId)).thenReturn(Optional.of(fortuneCookieDAO));
        when(userRepository.findByUsername(username)).thenReturn(userDAO);

        fortuneCookieService.removeLikeFortuneCookie(fortuneCookieId, username);

        assertFalse(fortuneCookieDAO.getLikes().contains(userDAO));
        verify(fortuneCookieRepository, times(1)).save(fortuneCookieDAO);
    }
}
