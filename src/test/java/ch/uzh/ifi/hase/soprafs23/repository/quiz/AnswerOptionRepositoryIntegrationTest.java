package ch.uzh.ifi.hase.soprafs23.repository.quiz;

import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

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
    }

    @AfterEach
    void emptyRepository() {
    }

    @Test
    void getAnswerOptionByAnswerOptionId() {
        //todo: test
    }


}