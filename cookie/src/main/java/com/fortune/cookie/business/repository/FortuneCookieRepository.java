package com.fortune.cookie.business.repository;

import com.fortune.cookie.business.repository.model.FortuneCookieDAO;
import com.fortune.cookie.business.repository.model.UserDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FortuneCookieRepository extends PagingAndSortingRepository<FortuneCookieDAO, Long> {

    Page<FortuneCookieDAO> findByUserDAO(UserDAO userDAO, Pageable pageable);
    List<FortuneCookieDAO> findByUserDAOAndDate(UserDAO userDAO, LocalDate today);
    Page<FortuneCookieDAO> findByDate(LocalDate today, Pageable pageable);

    FortuneCookieDAO save(FortuneCookieDAO fortuneCookieDAO);

    Optional<FortuneCookieDAO> findById(Long fortuneCookieId);

    void delete(FortuneCookieDAO fortuneCookieDAO);
}
