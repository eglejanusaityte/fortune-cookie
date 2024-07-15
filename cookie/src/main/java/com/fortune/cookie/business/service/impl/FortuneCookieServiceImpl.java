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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Iterator;

import lombok.extern.log4j.Log4j2;

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
    @Cacheable(value = "fortuneCookies", key = "#email + '_' + T(java.time.LocalDate).now().toString()")
    public FortuneCookie createFortuneCookie(String email) {
        Optional<UserDAO> user = Optional.ofNullable(userRepository.findByEmail(email));
        UserDAO userDAO = user.orElseThrow();

        LocalDate today = LocalDate.now();
        Optional<FortuneCookieDAO> existingFortuneCookieOptional = fortuneCookieRepository.findByUserDAOAndDate(userDAO, today);

        if (existingFortuneCookieOptional.isPresent()) {
            FortuneCookieDAO existingFortuneCookieDAO = existingFortuneCookieOptional.get();
            log.info("Getting existing fortune cookie: {}", existingFortuneCookieDAO);
            return fortuneCookieMapper.fortuneCookieDAOToFortuneCookie(existingFortuneCookieDAO);
        }

        List<FortuneDAO> allFortunes = fortuneRepository.findAll();
        FortuneDAO randomFortune = getRandomFortune(allFortunes);

        List<WordDAO> allWords = wordRepository.findAll();
        Set<WordDAO> randomWords = getRandomSubset(allWords, randomFortune.getNeededWordDAOS());

        FortuneCookieDAO fortuneCookieDAO = new FortuneCookieDAO();
        fortuneCookieDAO.setFortuneDAO(randomFortune);
        fortuneCookieDAO.setWords(randomWords);
        fortuneCookieDAO.setUserDAO(userDAO);

        FortuneCookieDAO savedFortuneCookieDAO = fortuneCookieRepository.save(fortuneCookieDAO);
        log.info("Creating new fortune cookie: {}", savedFortuneCookieDAO);
        return fortuneCookieMapper.fortuneCookieDAOToFortuneCookie(savedFortuneCookieDAO);
    }

    @Override
    public String createFortuneCookieShort(String email) {
        FortuneCookie fortuneCookie = createFortuneCookie(email);
        String sentence = fortuneCookie.getFortune().getSentence();
        for (Word word : fortuneCookie.getWords()) {
            String placeholder = "#" + word.getWordType();
            sentence = sentence.replaceFirst(placeholder, word.getWord());
        }
        return sentence;
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

    public FortuneCookie createFortuneCookiePersonal(Long fortuneId, String email, List<String> words) {
        Optional<UserDAO> user = Optional.ofNullable(userRepository.findByEmail(email));
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

        return fortuneCookieMapper.fortuneCookieDAOToFortuneCookie(fortuneCookieRepository.save(fortuneCookieDAO));
    }

    public List<ModifiedFortuneCookieDTO> getFortuneCookiesByUserId(String email) {
        Optional<UserDAO> user = Optional.ofNullable(userRepository.findByEmail(email));
        UserDAO userDAO = user.orElseThrow();
        List<FortuneCookieDAO> fortuneCookieDAOs = fortuneCookieRepository.findByUserDAO(userDAO);
        fortuneCookieDAOs.sort(new Comparator<FortuneCookieDAO>() {
            @Override
            public int compare(FortuneCookieDAO cookie1, FortuneCookieDAO cookie2) {
                return cookie2.getDate().compareTo(cookie1.getDate());
            }
        });

        List<ModifiedFortuneCookieDTO> modifiedFortuneCookies = new ArrayList<>();
        for (FortuneCookieDAO fortuneCookie : fortuneCookieDAOs) {
            String sentence = fortuneCookie.getFortuneDAO().getSentence();

            for (WordDAO word : fortuneCookie.getWords()) {
                String placeholder = "#" + word.getWordType();
                sentence = sentence.replaceFirst(placeholder, word.getWord());
            }
            ModifiedFortuneCookieDTO dto = new ModifiedFortuneCookieDTO(email, sentence, fortuneCookie.getDate());
            modifiedFortuneCookies.add(dto);
        }
        return modifiedFortuneCookies;
    }

    public List<FortuneCookie> getFortuneCookies() {
        List<FortuneCookieDAO> fortuneCookieDAOs = fortuneCookieRepository.findAll();
        return fortuneCookieDAOs.stream()
                .map(fortuneCookieMapper::fortuneCookieDAOToFortuneCookie)
                .collect(Collectors.toList());
    }

    public void deleteFortuneCookie(Long fortuneCookieId, String email) {
        FortuneCookieDAO fortuneCookieDAO = fortuneCookieRepository.findById(fortuneCookieId)
                .orElseThrow(() -> new NoSuchElementException("Fortune cookie not found"));

        Optional<UserDAO> user = Optional.ofNullable(userRepository.findByEmail(email));
        UserDAO userDAO = user.orElseThrow();

        if (!userDAO.getRole().equals(Role.USER) && !fortuneCookieDAO.getUserDAO().getId().equals(userDAO.getId())) {
            throw new IllegalArgumentException("User is not authorized to delete this fortune cookie");
        }

        fortuneCookieRepository.delete(fortuneCookieDAO);
    }

    @Override
    public void likeFortuneCookie(Long fortuneCookieId, String email) {
        FortuneCookieDAO fortuneCookieDAO = fortuneCookieRepository.findById(fortuneCookieId).orElse(null);
        Set<UserDAO> liked = fortuneCookieDAO.getLikes();
        UserDAO userDAO = userRepository.findByEmail(email);
        if(!liked.contains(userDAO)){
            liked.add(userDAO);
            fortuneCookieDAO.setLikes(liked);
            fortuneCookieRepository.save(fortuneCookieDAO);
        }
    }

    @Override
    public void removeLikeFortuneCookie(Long fortuneCookieId, String email) {
        FortuneCookieDAO fortuneCookieDAO = fortuneCookieRepository.findById(fortuneCookieId).orElse(null);
        Set<UserDAO> liked = fortuneCookieDAO.getLikes();
        UserDAO userDAO = userRepository.findByEmail(email);
        if(liked.contains(userDAO)){
            liked.remove(userDAO);
            fortuneCookieDAO.setLikes(liked);
            fortuneCookieRepository.save(fortuneCookieDAO);
        }
    }

}
