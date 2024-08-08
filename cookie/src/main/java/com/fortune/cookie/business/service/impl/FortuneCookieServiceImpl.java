package com.fortune.cookie.business.service.impl;

import com.fortune.cookie.business.dto.ModifiedFortuneCookieDTO;
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
import com.fortune.cookie.business.service.FortuneCookieService;
import com.fortune.cookie.model.FortuneCookie;
import com.fortune.cookie.model.User;
import com.fortune.cookie.model.Word;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Log4j2
public class FortuneCookieServiceImpl implements FortuneCookieService {

    @Autowired
    private FortuneCookieRepository fortuneCookieRepository;

    @Autowired
    private FortuneRepository fortuneRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FortuneCookieMapper fortuneCookieMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    @Cacheable(value = "fortuneCookies", key = "#username + '_' + T(java.time.LocalDate).now().toString()")
    public FortuneCookie createFortuneCookie(String username) {
        Optional<UserDAO> user = Optional.ofNullable(userRepository.findByUsername(username));
        UserDAO userDAO = user.orElseThrow();

        LocalDate today = LocalDate.now();
        List<FortuneCookieDAO> existingFortuneCookies = fortuneCookieRepository.findByUserDAOAndDate(userDAO, today);
        for(FortuneCookieDAO cookie : existingFortuneCookies){
            boolean personal = false;
            Optional<WordDAO> optional = cookie.getWords().stream().findFirst();
            if (optional.isPresent()) {
                WordDAO retrieved = optional.get();
                if(retrieved.getPersonal()){
                    personal = true;
                }
            }
            if(!personal)
            {
                log.info("Getting existing fortune cookie: {}", cookie);
                return fortuneCookieMapper.fortuneCookieDAOToFortuneCookie(cookie);
            }
        }

        List<FortuneDAO> allFortunes = fortuneRepository.findAll();
        FortuneDAO randomFortune = getRandomFortune(allFortunes);

        List<WordDAO> allWords = wordRepository.findAll();
        List<WordDAO> nonPersonalWords = allWords.stream()
                .filter(word -> !word.getPersonal())
                .toList();

        Set<WordDAO> randomWords = getRandomSubset(nonPersonalWords, randomFortune.getNeededWordDAOS());

        FortuneCookieDAO fortuneCookieDAO = new FortuneCookieDAO();
        fortuneCookieDAO.setFortuneDAO(randomFortune);
        fortuneCookieDAO.setWords(randomWords);
        fortuneCookieDAO.setUserDAO(userDAO);

        FortuneCookieDAO savedFortuneCookieDAO = fortuneCookieRepository.save(fortuneCookieDAO);
        log.info("Creating new fortune cookie: {}", savedFortuneCookieDAO);
        return fortuneCookieMapper.fortuneCookieDAOToFortuneCookie(savedFortuneCookieDAO);
    }

    @Override
    public ModifiedFortuneCookieDTO createFortuneCookieShort(String username) {
        FortuneCookie fortuneCookie = createFortuneCookie(username);
        String sentence = fortuneCookie.getFortune().getSentence();
        boolean personal = false;
        for (Word word : fortuneCookie.getWords()) {
            String placeholder = "#" + word.getWordType();
            sentence = sentence.replaceFirst(Pattern.quote(placeholder), word.getText());
            personal = word.getPersonal();
        }
        Set<String> usernames = Collections.emptySet();
        return new ModifiedFortuneCookieDTO(fortuneCookie.getId(), username, sentence, fortuneCookie.getDate(),
                personal, usernames);
    }

    private <T> T getRandomFortune(List<T> list) {
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }

    private Set<WordDAO> getRandomSubset(List<WordDAO> allWords, Set<NeededWordDAO> neededWords) {
        Random random = new Random();
        Set<WordDAO> subset = new HashSet<>();
        for (NeededWordDAO neededWord : neededWords) {
            List<WordDAO> matchingWords = allWords.stream()
                    .filter(word -> word.getWordType().equals(neededWord.getWordType()))
                    .toList();

            if (matchingWords.isEmpty()) {
                throw new NoSuchElementException("No matching words found for word type: " + neededWord.getWordType());
            } else {
                WordDAO randomWord = matchingWords.get(random.nextInt(matchingWords.size()));
                subset.add(randomWord);
            }
        }
        return subset;
    }

    public ModifiedFortuneCookieDTO createFortuneCookiePersonal(Long fortuneId, String username, List<String> words) {
        Optional<UserDAO> user = Optional.ofNullable(userRepository.findByUsername(username));
        UserDAO userDAO = user.orElseThrow();

        FortuneDAO fortuneDAO = fortuneRepository.findById(fortuneId)
                .orElseThrow(() -> new NoSuchElementException("Fortune not found"));

        if (words.size() != fortuneDAO.getNeededWordDAOS().size()) {
            throw new IllegalArgumentException("Number of provided words doesn't match number of needed words");
        }

        Set<WordDAO> savedWordDAOs = new HashSet<>();
        Iterator<NeededWordDAO> neededWordIterator = fortuneDAO.getNeededWordDAOS().iterator();
        for (String word : words) {
            if (!neededWordIterator.hasNext()) {
                throw new IllegalArgumentException("Not enough needed words specified");
            }
            NeededWordDAO neededWord = neededWordIterator.next();
            WordType wordType = neededWord.getWordType();

            WordDAO wordDAO = new WordDAO(word, wordType, true);
            savedWordDAOs.add(wordRepository.save(wordDAO));
        }

        FortuneCookieDAO fortuneCookieDAO = new FortuneCookieDAO();
        fortuneCookieDAO.setUserDAO(userDAO);
        fortuneCookieDAO.setFortuneDAO(fortuneDAO);
        fortuneCookieDAO.setWords(savedWordDAOs);
        FortuneCookieDAO savedFortuneCookieDAO = fortuneCookieRepository.save(fortuneCookieDAO);

        String sentence = savedFortuneCookieDAO.getFortuneDAO().getSentence();
        for (WordDAO word : savedFortuneCookieDAO.getWords()) {
            String placeholder = "#" + word.getWordType();
            sentence = sentence.replaceFirst(Pattern.quote(placeholder), word.getText());
        }

        ModifiedFortuneCookieDTO dto = new ModifiedFortuneCookieDTO(savedFortuneCookieDAO.getId(), username,
                sentence, savedFortuneCookieDAO.getDate(),true, new HashSet<>());
        return dto;
    }

    public Page<ModifiedFortuneCookieDTO> getFortuneCookiesByUserId(Integer page, String username) {
        Optional<UserDAO> user = Optional.ofNullable(userRepository.findByUsername(username));
        UserDAO userDAO = user.orElseThrow();
        Pageable cookiePage = PageRequest.of(page, 3, Sort.by("date").descending());
        Page<FortuneCookieDAO> fortuneCookieDAOs = fortuneCookieRepository.findByUserDAO(userDAO, cookiePage);

        List<ModifiedFortuneCookieDTO> modifiedFortuneCookies = new ArrayList<>();
        for (FortuneCookieDAO fortuneCookie : fortuneCookieDAOs) {
            String sentence = fortuneCookie.getFortuneDAO().getSentence();
            boolean personal = false;
            for (WordDAO word : fortuneCookie.getWords()) {
                String placeholder = "#" + word.getWordType();
                sentence = sentence.replaceFirst(Pattern.quote(placeholder), word.getText());
                personal = word.getPersonal();
            }
            Set<String> usernames = fortuneCookie.getLikes().stream()
                    .map(UserDAO::getUsername)
                    .collect(Collectors.toSet());
            ModifiedFortuneCookieDTO dto = new ModifiedFortuneCookieDTO(fortuneCookie.getId(), username, sentence,
                    fortuneCookie.getDate(), personal, usernames);
            modifiedFortuneCookies.add(dto);
        }
        long totalElements = fortuneCookieDAOs.getTotalElements();
        return new PageImpl<>(modifiedFortuneCookies, cookiePage, totalElements);
    }

    public Page<ModifiedFortuneCookieDTO> getFortuneCookies(Integer page) {
        LocalDate today = LocalDate.now();

        Pageable cookiePage = PageRequest.of(page, 3);
        Page<FortuneCookieDAO> fortuneCookieDAOs = fortuneCookieRepository.findByDate(today, cookiePage);

        List<ModifiedFortuneCookieDTO> modifiedFortuneCookies = fortuneCookieDAOs.stream()
                .map(fortuneCookie -> {
                    String sentence = fortuneCookie.getFortuneDAO().getSentence();
                    boolean personal = false;

                    for (WordDAO word : fortuneCookie.getWords()) {
                        String placeholder = "#" + word.getWordType();
                        sentence = sentence.replaceFirst(Pattern.quote(placeholder), word.getText());
                        personal = word.getPersonal();
                    }

                    Set<String> usernames = fortuneCookie.getLikes().stream()
                            .map(UserDAO::getUsername)
                            .collect(Collectors.toSet());

                    return new ModifiedFortuneCookieDTO(
                            fortuneCookie.getId(),
                            fortuneCookie.getUserDAO().getUsername(),
                            sentence,
                            fortuneCookie.getDate(),
                            personal,
                            usernames
                    );
                })
                .collect(Collectors.toList());

        long totalElements = fortuneCookieDAOs.getTotalElements();
        return new PageImpl<>(modifiedFortuneCookies, cookiePage, totalElements);
    }

    public void deleteFortuneCookie(Long fortuneCookieId, String username) {
        FortuneCookieDAO fortuneCookieDAO = fortuneCookieRepository.findById(fortuneCookieId)
                .orElseThrow(() -> new NoSuchElementException("Fortune cookie not found"));

        Optional<UserDAO> user = Optional.ofNullable(userRepository.findByUsername(username));
        UserDAO userDAO = user.orElseThrow();

        if (!userDAO.getRole().equals(Role.USER) && !fortuneCookieDAO.getUserDAO().getId().equals(userDAO.getId())) {
            throw new IllegalArgumentException("User is not authorized to delete this fortune cookie");
        }

        fortuneCookieRepository.delete(fortuneCookieDAO);
    }

    @Override
    public void likeFortuneCookie(Long fortuneCookieId, String username) {
        FortuneCookieDAO fortuneCookieDAO = fortuneCookieRepository.findById(fortuneCookieId).orElse(null);
        Set<UserDAO> liked = fortuneCookieDAO.getLikes();
        UserDAO userDAO = userRepository.findByUsername(username);
        if(!liked.contains(userDAO)){
            liked.add(userDAO);
            fortuneCookieDAO.setLikes(liked);
            fortuneCookieRepository.save(fortuneCookieDAO);
        }
    }

    @Override
    public void removeLikeFortuneCookie(Long fortuneCookieId, String username) {
        FortuneCookieDAO fortuneCookieDAO = fortuneCookieRepository.findById(fortuneCookieId).orElse(null);
        Set<UserDAO> liked = fortuneCookieDAO.getLikes();
        UserDAO userDAO = userRepository.findByUsername(username);
        if(liked.contains(userDAO)){
            liked.remove(userDAO);
            fortuneCookieDAO.setLikes(liked);
            fortuneCookieRepository.save(fortuneCookieDAO);
        }
    }

}
