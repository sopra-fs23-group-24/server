package ch.uzh.ifi.hase.soprafs23.repository.quiz;

import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class QuizAnswerRepositoryIntegrationTest {
    QuizAnswer testQuizAnswer;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setup() {
        //setup

        entityManager.merge(testQuizAnswer);
        entityManager.flush();
    }

    @AfterEach
    void emptyRepository() {
        entityManager.remove(testQuizAnswer);
        entityManager.flush();
    }




}