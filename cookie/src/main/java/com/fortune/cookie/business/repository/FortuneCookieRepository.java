package com.fortune.cookie.business.repository;

import com.fortune.cookie.business.repository.model.FortuneCookieDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FortuneCookieRepository extends JpaRepository<FortuneCookieDAO, Long> {
}
