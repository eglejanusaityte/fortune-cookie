package com.fortune.cookie.business.repository;

import com.fortune.cookie.business.repository.model.UserDAO;
import com.fortune.cookie.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserDAO, Long> {
    UserDAO findByEmail(String email);
}