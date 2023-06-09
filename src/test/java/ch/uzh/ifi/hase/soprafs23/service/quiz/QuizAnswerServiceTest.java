package ch.uzh.ifi.hase.soprafs23.service.quiz;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.AnswerOptionRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.QuizAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.QuizQuestionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

class QuizAnswerServiceTest {
    AnswerOption answerOption1;
    AnswerOption answerOption2;
    AnswerOption answerOption3;
    AnswerOption answerOption4;
    private Game testGame;
    private QuizQuestion testQuestion;
    private Player testPlayer;
    @Mock
    private AnswerOptionRepository answerOptionRepository;
    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameRepository gameRepository;


    @Mock
    private QuizQuestionRepository quizQuestionRepository;

    @Mock
    private QuizAnswerRepository quizAnswerRepository;
    @InjectMocks
    private QuizAnswerService quizAnswerService;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        testPlayer = new Player();
        testPlayer.setPlayerName("player");
        testPlayer.setAssociatedGamePin("123456");
        testPlayer.setToken("playerToken");
        testPlayer.setScore(0);
        Mockito.when(playerRepository.findByToken("playerToken")).thenReturn(testPlayer);
        Mockito.when(playerRepository.findByToken("invalidToken")).thenReturn(null);

        testGame = new Game();
        testGame.setGamePin("123456");
        testGame.setHostId(2L);
        testGame.addPlayer(testPlayer);
        testGame.setTimer(30);
        Mockito.when(gameRepository.findByGamePin("123456")).thenReturn(testGame);
        Mockito.when(gameRepository.findByGamePin("invalidPin")).thenReturn(null);

        answerOption1 = new AnswerOption();
        answerOption1.setAnswerOptionText("option 1");
        answerOption1.setAnswerOptionId(96L);
        answerOption2 = new AnswerOption();
        answerOption2.setAnswerOptionText("option 2");
        answerOption2.setAnswerOptionId(97L);
        answerOption3 = new AnswerOption();
        answerOption3.setAnswerOptionText("option 3");
        answerOption3.setAnswerOptionId(99L);
        answerOption4 = new AnswerOption();
        answerOption4.setAnswerOptionText("option 4");
        answerOption4.setAnswerOptionId(99L);
        List<AnswerOption> answerOptionList = List.of(answerOption1, answerOption2, answerOption3, answerOption4);
        Mockito.when(answerOptionRepository.getAnswerOptionByAnswerOptionId(96L)).thenReturn(answerOption1);
        Mockito.when(answerOptionRepository.getAnswerOptionByAnswerOptionId(97L)).thenReturn(answerOption2);


        testQuestion = new QuizQuestion();
        testQuestion.setAnswerOptions(answerOptionList);
        testQuestion.setCorrectAnswer(answerOption1);
        testQuestion.setQuizQuestionText("test text");
        testQuestion.setQuestionStatus(CompletionStatus.NOT_FINISHED);
        testQuestion.setAssociatedGamePin("123456");
        testQuestion.setAssociatedPrompt(new Prompt());
        testQuestion.setQuestionId(999L);

        Mockito.when(quizQuestionRepository.findByQuestionId(Mockito.anyLong())).thenReturn(testQuestion);
        Mockito.when(quizQuestionRepository.save(Mockito.any())).thenReturn(testQuestion);

    }

    @Test
    void addQuizAnswerToQuizQuestion_success() {
        QuizAnswer quizAnswer = new QuizAnswer();
        quizAnswer.setPickedAnswerOptionId(answerOption1.getAnswerOptionId());

        Mockito.when(quizAnswerRepository.save(Mockito.any())).thenReturn(quizAnswer);

        Assertions.assertEquals(CompletionStatus.NOT_FINISHED, testQuestion.getQuestionStatus());
        QuizAnswer newAnswer = quizAnswerService.addQuizAnswerToQuizQuestion(quizAnswer, testQuestion, testPlayer.getToken());

        Assertions.assertEquals(newAnswer.getAssociatedPlayer(), testPlayer);
        Assertions.assertEquals(1, testQuestion.getReceivedAnswers().size());
        Assertions.assertEquals(testQuestion.getReceivedAnswers().get(0).getAssociatedPlayer(), testPlayer);
    }


    @Test
    void addQuizAnswerToQuizQuestion_invalidToken() {
        QuizAnswer quizAnswer = new QuizAnswer();
        quizAnswer.setPickedAnswerOptionId(answerOption1.getAnswerOptionId());

        Assertions.assertEquals(CompletionStatus.NOT_FINISHED, testQuestion.getQuestionStatus());

        Assertions.assertThrows(ResponseStatusException.class, () -> quizAnswerService.addQuizAnswerToQuizQuestion(quizAnswer, testQuestion, "invalidToken"));
    }

    @Test
    void addQuizAnswerToQuizQuestion_alreadyAnsweredQuestion() {
        QuizAnswer quizAnswer = new QuizAnswer();
        quizAnswer.setPickedAnswerOptionId(answerOption1.getAnswerOptionId());
        quizAnswer.setAssociatedPlayer(testPlayer);
        testQuestion.addReceivedAnswer(quizAnswer);
        Assertions.assertEquals(CompletionStatus.NOT_FINISHED, testQuestion.getQuestionStatus());

        Assertions.assertThrows(ResponseStatusException.class, () -> quizAnswerService.addQuizAnswerToQuizQuestion(quizAnswer, testQuestion, testPlayer.getToken()));
    }

    @Test
    void calculateAndAddScore_correctAnswer() {
        QuizAnswer quizAnswer = new QuizAnswer();
        quizAnswer.setPickedAnswerOptionId(answerOption1.getAnswerOptionId());
        quizAnswer.setAssociatedPlayer(testPlayer);
        quizAnswer.setTimer(10);
        Assertions.assertEquals(0, testPlayer.getScore());

        int score = quizAnswerService.calculateAndAddScore(quizAnswer, testQuestion, testGame);

        Assertions.assertTrue(score > 0);
        Assertions.assertTrue(testPlayer.getScore() > 0);
    }

    @Test
    void calculateAndAddScore_incorrectAnswer() {
        QuizAnswer quizAnswer = new QuizAnswer();
        quizAnswer.setPickedAnswerOptionId(answerOption2.getAnswerOptionId());
        quizAnswer.setAssociatedPlayer(testPlayer);
        Assertions.assertEquals(0, testPlayer.getScore());

        int score = quizAnswerService.calculateAndAddScore(quizAnswer, testQuestion, testGame);

        Assertions.assertEquals(0, score);
        Assertions.assertEquals(0, testPlayer.getScore());
    }

    @Test
    void updateQuestionStatusIfAllAnswered_updateToFinished(){
        QuizAnswer quizAnswer = new QuizAnswer();
        quizAnswer.setPickedAnswerOptionId(answerOption1.getAnswerOptionId());
        quizAnswer.setAssociatedPlayer(testPlayer);
        quizAnswer.setTimer(10);
        testQuestion.addReceivedAnswer(quizAnswer);

        Assertions.assertEquals(CompletionStatus.NOT_FINISHED, testQuestion.getQuestionStatus());
        QuizQuestion updatedQuestion = quizAnswerService.updateQuestionStatusIfAllAnswered(testGame, testQuestion);
        Assertions.assertEquals(CompletionStatus.FINISHED, updatedQuestion.getQuestionStatus());
    }

    @Test
    void findGameByPin_success(){
        Assertions.assertNotNull(quizAnswerService.findGameByPin("123456"));
    }

    @Test
    void findGameByPin_invalidPin(){
        Assertions.assertThrows(ResponseStatusException.class, () -> quizAnswerService.findGameByPin("invalidPin"));
    }

    @Test
    void findQuestionById_success(){
        Assertions.assertNotNull(quizAnswerService.findQuestionById(999L));
    }

    @Test
    void findQuestionById_invalidId(){
        Mockito.when(quizQuestionRepository.findByQuestionId(Mockito.anyLong())).thenReturn(null);
        Assertions.assertThrows(ResponseStatusException.class, () -> quizAnswerService.findQuestionById(1L));
    }
}