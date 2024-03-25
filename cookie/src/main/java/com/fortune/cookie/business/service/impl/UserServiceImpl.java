package com.fortune.cookie.business.service.impl;

import com.fortune.cookie.business.repository.model.UserDAO;
import com.fortune.cookie.business.mappers.UserMapper;
import com.fortune.cookie.business.repository.UserRepository;
import com.fortune.cookie.business.service.UserService;
import com.fortune.cookie.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(User user) throws IllegalArgumentException {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists.");
        }
        String hashedPassword = hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        UserDAO userDAO = UserMapper.INSTANCE.userToUserDAO(user);
        userDAO = userRepository.save(userDAO);

        return UserMapper.INSTANCE.userDAOToUser(userDAO);
    }

    private String hashPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
