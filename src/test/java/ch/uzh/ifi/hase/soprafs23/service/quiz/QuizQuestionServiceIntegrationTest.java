package ch.uzh.ifi.hase.soprafs23.service.quiz;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;
import ch.uzh.ifi.hase.soprafs23.constant.DisplayType;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.AnswerOptionRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.QuizQuestionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@WebAppConfiguration
@SpringBootTest
class QuizQuestionServiceIntegrationTest {
    Game testGame;
    Player testPlayer;
    QuizQuestion testQuizQuestion;
    QuizAnswer testQuizAnswer;
    AnswerOption correctAnswerOption;

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Qualifier("quizQuestionRepository")
    @Autowired
    private QuizQuestionRepository qqRepository;

    @Qualifier("answerOptionRepository")
    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;


    @Autowired
    private QuizQuestionService quizQuestionService;

    @BeforeEach
    void setup() {
        qqRepository.deleteAll();
        answerOptionRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();

        testGame = new Game();
        testGame.setStatus(GameStatus.SELECTION);
        testGame.setGamePin("123456");
        testGame.setTimer(40);
        testGame = gameRepository.save(testGame);
        gameRepository.flush();

        testPlayer = new Player();
        testPlayer.setPlayerName("testPlayer");
        testPlayer.setToken("playerToken");
        testPlayer.setAssociatedGamePin(testGame.getGamePin());
        testPlayer = playerRepository.save(testPlayer);
        playerRepository.flush();

        testGame.addPlayer(testPlayer);
        testGame = gameRepository.save(testGame);
        gameRepository.flush();

        correctAnswerOption = new AnswerOption();
        correctAnswerOption.setAnswerOptionText("correct answer");

        testQuizQuestion = new QuizQuestion();
        testQuizQuestion.setAssociatedGamePin(testGame.getGamePin());
        testQuizQuestion.setQuizQuestionText("text");
        testQuizQuestion.setImageToDisplay("some image");
        testQuizQuestion.setStoryToDisplay(null);
        testQuizQuestion.setAnswerOptions(List.of(correctAnswerOption));
        testQuizQuestion.setAnswerDisplayType(DisplayType.IMAGE);
        testQuizQuestion.setCorrectAnswer(correctAnswerOption);
        testQuizQuestion.setQuestionStatus(CompletionStatus.NOT_FINISHED);
        testQuizQuestion = qqRepository.save(testQuizQuestion);
        qqRepository.flush();

        testQuizAnswer = new QuizAnswer();
        testQuizAnswer.setPickedAnswerOptionId(correctAnswerOption.getAnswerOptionId());
        testQuizAnswer.setTimer(40);
    }

    @Test
    void getQuizQuestions() {
        List<QuizQuestion> allFound = quizQuestionService.getQuizQuestions();

        Assertions.assertEquals(1, allFound.size());
        QuizQuestion found = allFound.get(0);

        Assertions.assertEquals(found.getQuestionId(), testQuizQuestion.getQuestionId());
    }

    @Test
    void getQuizQuestionsOfGame() {
        List<QuizQuestion> allFound = quizQuestionService.getQuizQuestionsOfGame(testGame.getGamePin());

        Assertions.assertEquals(1, allFound.size());
        QuizQuestion found = allFound.get(0);

        Assertions.assertEquals(found.getQuestionId(), testQuizQuestion.getQuestionId());
    }
}

