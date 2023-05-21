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
import ch.uzh.ifi.hase.soprafs23.repository.quiz.QuizAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.QuizQuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@WebAppConfiguration
@SpringBootTest
public class QuizAnswerServiceIntegrationTest {
    Game testGame;
    Player testPlayer;
    QuizQuestion testQuizQuestion;
    QuizAnswer testQuizAnswer;
    AnswerOption correctAnswerOption;

    @Qualifier("quizQuestionRepository")
    @Autowired
    private QuizQuestionRepository qqRepository;

    @Qualifier("answerOptionRepository")
    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Qualifier("quizAnswerRepository")
    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private QuizAnswerService quizAnswerService;

    @BeforeEach
    public void setup() {
        qqRepository.deleteAll();
        answerOptionRepository.deleteAll();
        quizAnswerRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();

        testGame = new Game();
        testGame.setStatus(GameStatus.SELECTION);
        testGame.setGamePin("123456");
        testGame = gameRepository.save(testGame);
        gameRepository.flush();

        testPlayer = new Player();
        testPlayer.setPlayerName("testPlayer");
        testPlayer.setToken("playerToken");
        testPlayer.setAssociatedGamePin("123456");
        testPlayer = playerRepository.save(testPlayer);
        playerRepository.flush();

        correctAnswerOption = new AnswerOption();
        correctAnswerOption.setAnswerOptionText("correct answer");

        testQuizQuestion = new QuizQuestion();
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
    public void addQuizAnswerToQuizQuestion() {
        //TODO: fix or delete
        /*Player foundPlayer = playerRepository.findByToken(testPlayer.getToken());
        Assertions.assertNotNull(foundPlayer);

        QuizQuestion foundQuizQuestion = qqRepository.findByQuestionId(testQuizQuestion.getQuestionId());
        Assertions.assertNotNull(foundQuizQuestion);

        QuizAnswer created = quizAnswerService.addQuizAnswerToQuizQuestion(testQuizAnswer, testQuizQuestion, testPlayer.getToken());
        Assertions.assertEquals(testQuizAnswer.getPickedAnswerOptionId(), created.getPickedAnswerOptionId());
        Assertions.assertEquals(testQuizAnswer.getTimer(), created.getTimer());
        Assertions.assertEquals(testQuizAnswer.getAssociatedPlayer().getPlayerId(), testPlayer.getPlayerId());
        Assertions.assertNotNull(created.getQuizAnswerId());

        foundQuizQuestion = qqRepository.findByQuestionId(testQuizQuestion.getQuestionId());
        Hibernate.initialize(foundQuizQuestion.getReceivedAnswers());
        Assertions.assertEquals(foundQuizQuestion.getReceivedAnswers().size(), 1);*/
    }

    @Test
    public void calculateAndAddScore() {
        //TODO: fix or delete
        /*testQuizAnswer.setAssociatedPlayer(testPlayer);

        testQuizQuestion.addReceivedAnswer(testQuizAnswer);
        testQuizQuestion.setQuestionStatus(CompletionStatus.NOT_FINISHED);
        testQuizQuestion = qqRepository.save(testQuizQuestion);
        qqRepository.flush();

        Assertions.assertEquals(testPlayer.getScore(), 0);

        Integer receivedScore = quizAnswerService.calculateAndAddScore(testQuizAnswer, testQuizQuestion, testGame);
        Assertions.assertEquals(receivedScore, 40);*/
    }

    @Test
    public void updateQuestionStatusIfAllAnswered() {

    }
}