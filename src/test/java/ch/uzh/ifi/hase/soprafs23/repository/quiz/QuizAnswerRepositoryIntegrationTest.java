package ch.uzh.ifi.hase.soprafs23.repository.quiz;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizAnswer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class QuizAnswerRepositoryIntegrationTest {
    QuizAnswer testQuizAnswer;
    Player testPlayer;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setup() {
        testPlayer = new Player();
        testPlayer.setPlayerName("test");
        testPlayer.setAssociatedGamePin("123456");
        testPlayer.setHost(true);
        testPlayer.setToken("1");
        testPlayer.setPlayerId(1L);

        testPlayer = entityManager.merge(testPlayer);
        entityManager.flush();

        testQuizAnswer = new QuizAnswer();
        testQuizAnswer.setQuizAnswerId(999L);
        testQuizAnswer.setPickedAnswerOptionId(900L);
        testQuizAnswer.setAssociatedPlayer(testPlayer);
        testQuizAnswer.setTimer(10);

        testQuizAnswer = entityManager.merge(testQuizAnswer);
        entityManager.flush();
    }

    @AfterEach
    void emptyRepository() {
        quizAnswerRepository.deleteAll();
    }

    @Test
    void getAnswerOptionByAnswerOptionId() {
        QuizAnswer found = quizAnswerRepository.findByQuizAnswerId(testQuizAnswer.getQuizAnswerId());

        assertEquals(testQuizAnswer.getPickedAnswerOptionId(), found.getPickedAnswerOptionId());
        assertEquals(testQuizAnswer.getTimer(), found.getTimer());
        assertEquals(testQuizAnswer.getAssociatedPlayer(), found.getAssociatedPlayer());
    }

    @Test
    void findByAssociatedPlayer() {
        QuizAnswer found = quizAnswerRepository.findByAssociatedPlayer(testQuizAnswer.getAssociatedPlayer());

        assertEquals(testQuizAnswer.getPickedAnswerOptionId(), found.getPickedAnswerOptionId());
        assertEquals(testQuizAnswer.getTimer(), found.getTimer());
        assertEquals(testQuizAnswer.getAssociatedPlayer(), found.getAssociatedPlayer());
    }

}