package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class GameRepositoryIntegrationTest {

    private Game game;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    @BeforeEach
    void setup() {
        QuizQuestion testQuizQuestion = new QuizQuestion();
        testQuizQuestion.setQuizQuestionText("test text");
        testQuizQuestion.setAssociatedGamePin("123456");

        testQuizQuestion = entityManager.merge(testQuizQuestion);
        entityManager.flush();

        Prompt testPrompt = new Prompt();
        testPrompt.setPromptNr(999);
        testPrompt.setPromptText("prompt text");
        testPrompt.setPromptType(PromptType.DRAWING);
        testPrompt = entityManager.merge(testPrompt);
        entityManager.flush();

        game = new Game();
        game.setGamePin("123456");
        game.setStatus(GameStatus.LOBBY);
        game.setHostId(1L);
        game.setTimer(30);
        game.setPromptSet(List.of(testPrompt));
        game.setQuizQuestionSet(List.of(testQuizQuestion));
        game.setCurrentQuestion(testQuizQuestion);

        game = entityManager.persist(game);
        entityManager.flush();
    }

    @Test
    public void findByGamePin_success() {
        Game found = gameRepository.findByGamePin(game.getGamePin());

        assertNotNull(found.getGameId());
        assertEquals(game.getGamePin(), found.getGamePin());
        assertEquals(game.getStatus(), found.getStatus());
        assertEquals(game.getHostId(), found.getHostId());
        assertEquals(new ArrayList<Player>(), found.getPlayerGroup());
        assertEquals(game.getPromptSet(), found.getPromptSet());
        assertEquals(game.getQuizQuestionSet(), found.getQuizQuestionSet());
        assertEquals(game.getCurrentQuestion(), found.getCurrentQuestion());
    }

    @Test
    public void deleteByGamePin_success() {
        Game found = gameRepository.findByGamePin(game.getGamePin());

        assertNotNull(found);

        gameRepository.deleteByGamePin(game.getGamePin());

        assertNull(gameRepository.findByGamePin(game.getGamePin()));
    }

}