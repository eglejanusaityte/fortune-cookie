package com.fortune.cookie.business.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fortune_cookie")
public class FortuneCookieDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @CreationTimestamp
    @Column(name = "date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "fortune_id")
    private FortuneDAO fortuneDAO;

    @ManyToMany
    @JoinTable(
            name = "fortune_cookie_word",
            joinColumns = @JoinColumn(name = "fortune_cookie_id"),
            inverseJoinColumns = @JoinColumn(name = "word_id")
    )
    private Set<WordDAO> words;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDAO userDAO;

    @ManyToMany
    @JoinTable(
            name = "fortune_cookie_likes",
            joinColumns = @JoinColumn(name = "fortune_cookie_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserDAO> likes;

}