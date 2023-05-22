package ch.uzh.ifi.hase.soprafs23.service.quiz;

import ch.uzh.ifi.hase.soprafs23.constant.CompletionStatus;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.PromptType;
import ch.uzh.ifi.hase.soprafs23.constant.QuestionType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.DrawingPromptAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.PotentialQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.prompt.Prompt;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.AnswerOption;
import ch.uzh.ifi.hase.soprafs23.entity.quiz.QuizQuestion;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.DrawingPromptAnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.PotentialQuestionRepository;
import ch.uzh.ifi.hase.soprafs23.repository.prompt.PromptRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.AnswerOptionRepository;
import ch.uzh.ifi.hase.soprafs23.repository.quiz.QuizQuestionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class QuizQuestionGeneratorDrawTest {
    private final List<Player> testPlayers = new ArrayList<>();
    private final List<DrawingPromptAnswer> testDrawingAnswers = new ArrayList<>();
    private Prompt drawTestPrompt;
    private PotentialQuestion potentialDrawingQuestionPROMPTANSWER;
    private PotentialQuestion potentialDrawingQuestionPLAYER;
    private QuizQuestion testQuizQuestion;
    private Game testGame;
    @Mock
    private QuizQuestionRepository qqRepository;
    @Mock
    private PotentialQuestionRepository potentialQuestionRepository;
    @Mock
    private DrawingPromptAnswerRepository drawingPromptAnswerRepository;
    @Mock
    private AnswerOptionRepository answerOptionRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private PromptRepository promptRepository;



    @InjectMocks
    private QuizQuestionGenerator quizQuestionGenerator;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testGame = new Game();
        testGame.setGamePin("123456");
        testGame.setStatus(GameStatus.PROMPT);
        setUpPlayers();
        testGame.setPlayerGroup(testPlayers);
        testGame.setHostId(testPlayers.get(0).getPlayerId());
        setUpPromptAndPotentialQuestion();
        testGame.setPromptSet(List.of(drawTestPrompt));
        Mockito.when(gameRepository.findByGamePin(Mockito.anyString())).thenReturn(testGame);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);

        setUpDrawingPromptAnswers();

        Mockito.when(qqRepository.save(Mockito.any())).thenReturn(testQuizQuestion);

    }

    @Test
    public void generateQuestionForTextPrompt(){
        QuizQuestion generatedQuestion = quizQuestionGenerator.generateQuestionForDrawingPrompt(drawTestPrompt, testPlayers.get(0), testGame);
        Assertions.assertNotNull(generatedQuestion);
    }

    @Test
    public void createQuizQuestions(){
        /*List<QuizQuestion> generatedQuestions = quizQuestionGenerator.createQuizQuestions(testGame.getGamePin());
        Assertions.assertEquals(4, generatedQuestions.size());*/
    }
    @Test
    public void transformPotentialQuestionDrawing_PLAYER() {
        QuizQuestion generatedQuestion = quizQuestionGenerator.transformPotentialDrawingQuestionTypePlayer(potentialDrawingQuestionPLAYER, new ArrayList<>(testDrawingAnswers), testPlayers.get(0));

        Assertions.assertEquals(generatedQuestion.getQuestionStatus(), CompletionStatus.NOT_FINISHED);

        Assertions.assertEquals(generatedQuestion.getAnswerOptions().size(), 4);
        for (AnswerOption ao : generatedQuestion.getAnswerOptions()) {
            assert (ao.getAnswerOptionText().equals(testPlayers.get(0).getPlayerName())
                    || ao.getAnswerOptionText().equals(testPlayers.get(1).getPlayerName())
                    || ao.getAnswerOptionText().equals(testPlayers.get(2).getPlayerName())
                    || ao.getAnswerOptionText().equals(testPlayers.get(3).getPlayerName())
            );
        }
        Assertions.assertNotNull(generatedQuestion.getQuizQuestionText());
        Assertions.assertEquals(generatedQuestion.getQuizQuestionText(), potentialDrawingQuestionPLAYER.getQuestionText());

        Assertions.assertEquals(generatedQuestion.getCorrectAnswer().getAnswerOptionText(), testPlayers.get(0).getPlayerName());
        Assertions.assertTrue(generatedQuestion.getAnswerOptions().contains(generatedQuestion.getCorrectAnswer()));

        Assertions.assertTrue(generatedQuestion.getReceivedAnswers().isEmpty());

        Assertions.assertNotNull(generatedQuestion.getQuizQuestionText());
        Assertions.assertEquals(generatedQuestion.getImageToDisplay(), "some drawing from: " + testPlayers.get(0).getPlayerName());
        Assertions.assertNull(generatedQuestion.getStoryToDisplay());
    }

    @Test
    public void transformPotentialQuestionDrawing_PROMPANSWER() {

        QuizQuestion generatedQuestion = quizQuestionGenerator.transformPotentialDrawingQuestionTypePromptAnswer(potentialDrawingQuestionPROMPTANSWER, new ArrayList<>(testDrawingAnswers), testPlayers.get(0));

        Assertions.assertEquals(generatedQuestion.getQuestionStatus(), CompletionStatus.NOT_FINISHED);

        Assertions.assertEquals(generatedQuestion.getAnswerOptions().size(), 4);
        for (AnswerOption ao : generatedQuestion.getAnswerOptions()) {
            assert (ao.getAnswerOptionText().equals(testDrawingAnswers.get(0).getAnswerDrawing())
                    || ao.getAnswerOptionText().equals(testDrawingAnswers.get(1).getAnswerDrawing())
                    || ao.getAnswerOptionText().equals(testDrawingAnswers.get(2).getAnswerDrawing())
                    || ao.getAnswerOptionText().equals(testDrawingAnswers.get(3).getAnswerDrawing())
            );
        }

        Assertions.assertNotNull(generatedQuestion.getQuizQuestionText());

        Assertions.assertEquals(generatedQuestion.getCorrectAnswer().getAnswerOptionText(), "some drawing from: " + testPlayers.get(0).getPlayerName());
        Assertions.assertTrue(generatedQuestion.getAnswerOptions().contains(generatedQuestion.getCorrectAnswer()));

        Assertions.assertTrue(generatedQuestion.getReceivedAnswers().isEmpty());

        Assertions.assertNull(generatedQuestion.getImageToDisplay());
        Assertions.assertNull(generatedQuestion.getStoryToDisplay());
    }


    /**
     * Set Up Methods, called in setup
     */
    private void setUpPlayers() {
        Player host = new Player();
        host.setPlayerName("host");
        host.setAssociatedGamePin("123456");
        host.setPlayerId(1L);
        host.setHost(true);
        Mockito.when(playerRepository.findByPlayerId(1L)).thenReturn(host);


        Player player2 = new Player();
        player2.setPlayerName("player2");
        player2.setAssociatedGamePin("123456");
        player2.setPlayerId(2L);
        player2.setHost(false);
        Mockito.when(playerRepository.findByPlayerId(2L)).thenReturn(player2);


        Player player3 = new Player();
        player3.setPlayerName("player3");
        player3.setAssociatedGamePin("123456");
        player3.setPlayerId(3L);
        player3.setHost(false);
        Mockito.when(playerRepository.findByPlayerId(3L)).thenReturn(player3);


        Player player4 = new Player();
        player4.setPlayerName("player4");
        player4.setAssociatedGamePin("123456");
        player4.setPlayerId(4L);
        player4.setHost(false);
        Mockito.when(playerRepository.findByPlayerId(4L)).thenReturn(player4);

        testPlayers.add(host);
        testPlayers.add(player2);
        testPlayers.add(player3);
        testPlayers.add(player4);
    }

    private void setUpPromptAndPotentialQuestion() {
        drawTestPrompt = new Prompt();
        drawTestPrompt.setPromptNr(997);
        drawTestPrompt.setPromptText("Draw something.");
        drawTestPrompt.setPromptType(PromptType.DRAWING);
        Mockito.when(promptRepository.findByPromptNr(997)).thenReturn(drawTestPrompt);

        potentialDrawingQuestionPROMPTANSWER = new PotentialQuestion();
        potentialDrawingQuestionPROMPTANSWER.setQuestionText("Which rat was drawn by %s?");
        potentialDrawingQuestionPROMPTANSWER.setQuestionType(QuestionType.PROMPTANSWER);
        potentialDrawingQuestionPROMPTANSWER.setAssociatedPrompt(promptRepository.findByPromptNr(997));
        potentialDrawingQuestionPROMPTANSWER.setRequiresTextInput(true);

        potentialDrawingQuestionPLAYER = new PotentialQuestion();
        potentialDrawingQuestionPLAYER.setQuestionText("Which player drew this rat?");
        potentialDrawingQuestionPLAYER.setQuestionType(QuestionType.PLAYER);
        potentialDrawingQuestionPLAYER.setAssociatedPrompt(promptRepository.findByPromptNr(997));
        potentialDrawingQuestionPLAYER.setRequiresTextInput(false);

        Mockito.when(potentialQuestionRepository.findAllByAssociatedPrompt(drawTestPrompt)).thenReturn(List.of(potentialDrawingQuestionPLAYER, potentialDrawingQuestionPROMPTANSWER));
    }

    private void setUpDrawingPromptAnswers() {
        for (Player player : testPlayers) {
            DrawingPromptAnswer drawAnswer = new DrawingPromptAnswer();
            drawAnswer.setAnswerDrawing("some drawing from: " + player.getPlayerName());
            drawAnswer.setAssociatedPromptNr(drawTestPrompt.getPromptNr());
            drawAnswer.setAssociatedGamePin(testGame.getGamePin());
            drawAnswer.setAssociatedPlayerId(player.getPlayerId());
            drawAnswer.setUsedAsCorrectAnswer(false);
            testDrawingAnswers.add(drawAnswer);
        }
        Mockito.when(drawingPromptAnswerRepository.findDrawingPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(testPlayers.get(0).getPlayerId(), drawTestPrompt.getPromptNr())).thenReturn(testDrawingAnswers.get(0));
        Mockito.when(drawingPromptAnswerRepository.findDrawingPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(testPlayers.get(1).getPlayerId(), drawTestPrompt.getPromptNr())).thenReturn(testDrawingAnswers.get(1));
        Mockito.when(drawingPromptAnswerRepository.findDrawingPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(testPlayers.get(2).getPlayerId(), drawTestPrompt.getPromptNr())).thenReturn(testDrawingAnswers.get(2));
        Mockito.when(drawingPromptAnswerRepository.findDrawingPromptAnswerByAssociatedPlayerIdAndAssociatedPromptNr(testPlayers.get(3).getPlayerId(), drawTestPrompt.getPromptNr())).thenReturn(testDrawingAnswers.get(3));

        Mockito.when(drawingPromptAnswerRepository.findAllByAssociatedGamePinAndAssociatedPromptNr(testGame.getGamePin(), drawTestPrompt.getPromptNr())).thenReturn(testDrawingAnswers);

        Mockito.when(drawingPromptAnswerRepository.save(testDrawingAnswers.get(0))).thenReturn(testDrawingAnswers.get(0));
        Mockito.when(drawingPromptAnswerRepository.save(testDrawingAnswers.get(1))).thenReturn(testDrawingAnswers.get(1));
        Mockito.when(drawingPromptAnswerRepository.save(testDrawingAnswers.get(2))).thenReturn(testDrawingAnswers.get(2));
        Mockito.when(drawingPromptAnswerRepository.save(testDrawingAnswers.get(3))).thenReturn(testDrawingAnswers.get(3));

    }

}