package com.fortune.cookie.business.repository;

import com.fortune.cookie.business.repository.model.FortuneCookieDAO;
import com.fortune.cookie.business.repository.model.UserDAO;
import com.fortune.cookie.model.FortuneCookie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FortuneCookieRepository extends JpaRepository<FortuneCookieDAO, Long> {
    List<FortuneCookieDAO> findByUserDAO(UserDAO userDAO);

    Optional<FortuneCookieDAO> findByUserDAOAndDate(UserDAO userDAO, LocalDate today);
}
