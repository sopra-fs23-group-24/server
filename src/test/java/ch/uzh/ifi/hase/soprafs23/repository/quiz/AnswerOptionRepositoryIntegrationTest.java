package ch.uzh.ifi.hase.soprafs23.repository.quiz;

import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

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
        testAnswerOption = new AnswerOption();
        testAnswerOption.setAnswerOptionId(999L);
        testAnswerOption.setAnswerOptionText("here's an answer you can pick");

        testAnswerOption = entityManager.merge(testAnswerOption);
        entityManager.flush();
    }

    @AfterEach
    void emptyRepository() {
        answerOptionRepository.deleteAll();
    }

    @Test
    void getAnswerOptionByAnswerOptionId() {
        AnswerOption found = answerOptionRepository.getAnswerOptionByAnswerOptionId(testAnswerOption.getAnswerOptionId());

        assertEquals(testAnswerOption.getAnswerOptionText(), found.getAnswerOptionText());
    }


}