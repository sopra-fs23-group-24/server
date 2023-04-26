package ch.uzh.ifi.hase.soprafs23.service.quiz;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.PotentialQuestionRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.PromptRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.AnswerOptionRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.QuizQuestionRepository;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.service.prompt.PromptService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

/**
 * Test class for the UserResource REST resource.
 *
 * @see PlayerService
 */
@WebAppConfiguration
@SpringBootTest
public class QuizQuestionServiceIntegrationTest {
    QuizQuestion testQuestion;
    Game testGame;
    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Qualifier("quizQuestionRepository")
    @Autowired
    private QuizQuestionRepository qqRepository;

    @Qualifier("promptRepository")
    @Autowired
    private PromptRepository promptRepository;

    @Qualifier("answerOptionRepository")
    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Autowired
    private QuizQuestionService quizQuestionService;

    @BeforeEach
    private void setup(){}

    @Test
    public void getQuizQuestions(){}

    @Test
    public void pickPrompts_success(){}
}