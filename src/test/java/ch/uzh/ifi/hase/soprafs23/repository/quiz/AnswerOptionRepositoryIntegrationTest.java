package ch.uzh.ifi.hase.soprafs23.repository.quiz;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class AnswerOptionRepositoryIntegrationTest {
    AnswerOption testAnswerOption;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setup() {
        //setup

        entityManager.merge(testAnswerOption);
        entityManager.flush();
    }

    @AfterEach
    void emptyRepository() {
        entityManager.remove(testAnswerOption);
        entityManager.flush();
    }




}