package com.fortune.cookie.business.repository;

import com.fortune.cookie.business.repository.model.CommentDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentDAO, Long> {
}